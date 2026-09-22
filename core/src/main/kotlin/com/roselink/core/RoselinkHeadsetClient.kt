package com.roselink.core

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanFilter
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.os.Build
import android.os.ParcelUuid

/**
 * 去 UI 的最小控制核心：只负责 BLE 通道与配置帧，不含任何界面代码。
 *
 * 需要权限（Android 12+）：`BLUETOOTH_SCAN`、`BLUETOOTH_CONNECT`；
 * Android 11 及以下需要 `ACCESS_FINE_LOCATION`。
 *
 * 典型用法见 README。
 */
@SuppressLint("MissingPermission")
class RoselinkHeadsetClient(private val context: Context) {

    enum class State { IDLE, SCANNING, CONNECTING, READY, DISCONNECTED }

    var onStateChanged: ((State) -> Unit)? = null
    var onDeviceFound: ((BluetoothDevice, Int) -> Unit)? = null
    var onConfigFrame: ((ConfigFrame) -> Unit)? = null
    var onDeviceConfig: ((DeviceConfig) -> Unit)? = null

    /** 协商后的 MTU，未协商时为默认值 23。 */
    var mtu: Int = 23
        private set

    private val adapter: BluetoothAdapter?
        get() = (context.getSystemService(Context.BLUETOOTH_SERVICE) as? BluetoothManager)?.adapter

    private var gatt: BluetoothGatt? = null
    private var writeChar: BluetoothGattCharacteristic? = null
    private var notifyChar: BluetoothGattCharacteristic? = null

    private fun setState(state: State) {
        onStateChanged?.invoke(state)
    }

    // ------------------------------------------------------------------ 扫描

    private val scanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            onDeviceFound?.invoke(result.device, result.rssi)
        }
    }

    /**
     * 开始扫描。
     *
     * @param filterByService 是否只上报带 [RoselinkUuids.SERVICE] 的设备，默认 true。
     */
    fun startScan(filterByService: Boolean = true) {
        val scanner = adapter?.bluetoothLeScanner ?: return
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()
        val filters = if (filterByService) {
            listOf(ScanFilter.Builder().setServiceUuid(ParcelUuid(RoselinkUuids.SERVICE)).build())
        } else {
            emptyList()
        }
        scanner.startScan(filters, settings, scanCallback)
        setState(State.SCANNING)
    }

    fun stopScan() {
        adapter?.bluetoothLeScanner?.stopScan(scanCallback)
        if (gatt == null) setState(State.IDLE)
    }

    // ------------------------------------------------------------------ 连接

    fun connect(device: BluetoothDevice) {
        stopScan()
        setState(State.CONNECTING)
        gatt = device.connectGatt(context, false, gattCallback, BluetoothDevice.TRANSPORT_LE)
    }

    fun disconnect() {
        gatt?.disconnect()
    }

    /** 释放资源，调用后需重新 [connect]。 */
    fun close() {
        gatt?.close()
        gatt = null
        writeChar = null
        notifyChar = null
        mtu = 23
        setState(State.DISCONNECTED)
    }

    private val gattCallback = object : BluetoothGattCallback() {

        override fun onConnectionStateChange(g: BluetoothGatt, status: Int, newState: Int) {
            when (newState) {
                BluetoothProfile.STATE_CONNECTED -> g.discoverServices()
                BluetoothProfile.STATE_DISCONNECTED -> {
                    g.close()
                    if (gatt === g) gatt = null
                    setState(State.DISCONNECTED)
                }
            }
        }

        override fun onServicesDiscovered(g: BluetoothGatt, status: Int) {
            val service = g.getService(RoselinkUuids.SERVICE) ?: return
            writeChar = service.getCharacteristic(RoselinkUuids.WRITE)
            notifyChar = service.getCharacteristic(RoselinkUuids.NOTIFY)
            if (writeChar == null || notifyChar == null) return
            g.requestMtu(DEFAULT_MTU_REQUEST)
        }

        override fun onMtuChanged(g: BluetoothGatt, mtuSize: Int, status: Int) {
            mtu = mtuSize.coerceIn(RcspCommands.BLE_MTU_MIN, RcspCommands.BLE_MTU_MAX)
            enableNotifications(g)
        }

        override fun onDescriptorWrite(g: BluetoothGatt, d: BluetoothGattDescriptor, status: Int) {
            if (d.uuid == RoselinkUuids.CCCD) setState(State.READY)
        }

        @Deprecated("Deprecated in API 33")
        override fun onCharacteristicChanged(g: BluetoothGatt, c: BluetoothGattCharacteristic) {
            val value = c.value ?: return
            dispatchNotification(c.uuid, value)
        }

        override fun onCharacteristicChanged(
            g: BluetoothGatt,
            c: BluetoothGattCharacteristic,
            value: ByteArray,
        ) {
            dispatchNotification(c.uuid, value)
        }
    }

    private fun dispatchNotification(uuid: java.util.UUID, value: ByteArray) {
        if (uuid != RoselinkUuids.NOTIFY) return
        val frame = runCatching { ConfigFrameCodec.parse(value) }.getOrNull() ?: return
        onConfigFrame?.invoke(frame)
        onDeviceConfig?.invoke(DeviceConfigParser.parse(frame))
    }

    private fun enableNotifications(g: BluetoothGatt) {
        val c = notifyChar ?: return
        if (!g.setCharacteristicNotification(c, true)) return
        val descriptor = c.getDescriptor(RoselinkUuids.CCCD) ?: return
        writeDescriptor(g, descriptor, BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE)
    }

    // ------------------------------------------------------------------ 发送

    /** 写入原始字节到 WRITE 特征。 */
    fun write(payload: ByteArray): Boolean {
        val g = gatt ?: return false
        val c = writeChar ?: return false
        return writeCharacteristic(g, c, payload)
    }

    /** 写入一帧配置数据。 */
    fun write(frame: ConfigFrame): Boolean = write(ConfigFrameCodec.build(frame))

    /**
     * 请求设备回传设备配置。
     *
     * TODO: 原 App 该请求在 Dart 侧组装，具体报文尚未确认；
     * 目前先发送 [RcspCommands.GET_TARGET_INFO] 作为占位。
     * 请用 btsnoop 抓包后修正（见 docs/protocol.md「未确认项」）。
     */
    fun requestDeviceConfig(): Boolean = write(byteArrayOf(RcspCommands.GET_TARGET_INFO.toByte()))

    // ------------------------------------------------- Android 版本差异封装

    @Suppress("DEPRECATION")
    private fun writeCharacteristic(
        g: BluetoothGatt,
        c: BluetoothGattCharacteristic,
        payload: ByteArray,
    ): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        g.writeCharacteristic(c, payload, BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT) ==
            BluetoothGatt.GATT_SUCCESS
    } else {
        @Suppress("DEPRECATION")
        fun legacy(): Boolean {
            c.writeType = BluetoothGattCharacteristic.WRITE_TYPE_DEFAULT
            c.value = payload
            return g.writeCharacteristic(c)
        }
        legacy()
    }

    @Suppress("DEPRECATION")
    private fun writeDescriptor(
        g: BluetoothGatt,
        d: BluetoothGattDescriptor,
        value: ByteArray,
    ): Boolean = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        g.writeDescriptor(d, value) == BluetoothGatt.GATT_SUCCESS
    } else {
        @Suppress("DEPRECATION")
        fun legacy(): Boolean {
            d.value = value
            return g.writeDescriptor(d)
        }
        legacy()
    }

    companion object {
        /** 常用 MTU 请求值（协议允许上限 509）。 */
        private const val DEFAULT_MTU_REQUEST = 247
    }
}
