package com.roselink.core

/**
 * 通信层命令字（1 字节）。
 *
 * OTA 区段（0xE1–0xE8）仅作常量保留，本仓库不实现其流程。
 */
object RcspCommands {

    /** 数据通道。 */
    const val DATA = 0x01

    /** 获取目标特性表。 */
    const val GET_TARGET_FEATURE_MAP = 0x02

    /** 获取目标信息。 */
    const val GET_TARGET_INFO = 0x03

    /** 断开经典蓝牙。 */
    const val DISCONNECT_CLASSIC_BLUETOOTH = 0x06

    /** 切换通信通道请求。 */
    const val SWITCH_DEVICE_REQUEST = 0x0B

    /** 广播：设备通知（TWS）。 */
    const val ADVERTISEMENT_DEVICE_NOTIFY = 0xC2

    /** 广播：设置通知（TWS）。 */
    const val ADVERTISEMENT_SETTINGS_NOTIFY = 0xC3

    /** 广播：设备请求操作（TWS）。 */
    const val ADVERTISEMENT_DEV_REQUEST_OPERATION = 0xC4

    /** 设置通信 MTU。 */
    const val SETTINGS_COMMUNICATION_MTU = 0xD1

    /** 获取设备 MD5。 */
    const val GET_DEV_MD5 = 0xD4

    /** 自定义（RCSP 命令载体）。 */
    const val CUSTOM = 0xF0

    /** 扩展自定义。 */
    const val EXTRA_CUSTOM = 0xFF

    // ---------------- OTA（不实现，仅保留常量） ----------------

    const val OTA_GET_UPDATE_FILE_INFO_OFFSET = 0xE1
    const val OTA_INQUIRE_DEVICE_IF_CAN_UPDATE = 0xE2
    const val OTA_ENTER_UPDATE_MODE = 0xE3
    const val OTA_EXIT_UPDATE_MODE = 0xE4
    const val OTA_SEND_FIRMWARE_UPDATE_BLOCK = 0xE5
    const val OTA_GET_DEVICE_REFRESH_FIRMWARE_STATUS = 0xE6
    const val REBOOT_DEVICE = 0xE7
    const val OTA_NOTIFY_UPDATE_CONTENT_SIZE = 0xE8

    // ---------------- 传输层参数 ----------------

    const val BLE_MTU_MIN = 20
    const val BLE_MTU_MAX = 509

    const val CONNECT_TIMEOUT_MS = 40_000L
    const val DEFAULT_SCAN_TIMEOUT_MS = 8_000L
    const val DEFAULT_SEND_CMD_TIMEOUT_MS = 3_000L
    const val SEND_DATA_MAX_TIMEOUT_MS = 6_000L
    const val RECEIVE_OTA_CMD_TIMEOUT_MS = 20_000L

    const val PHY_LE_1M_MASK = 0x1
    const val PHY_LE_2M_MASK = 0x2
    const val PHY_LE_CODED_MASK = 0x4

    const val PROTOCOL_TYPE_BLE = 0
    const val PROTOCOL_TYPE_SPP = 1
    const val PROTOCOL_TYPE_GATT_OVER_BR_EDR = 2

    const val SCAN_TYPE_BLE = 0
    const val SCAN_TYPE_CLASSIC = 1
    const val SCAN_TYPE_ALL = 2
}
