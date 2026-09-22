# 命令字与传输层参数

## 1. 命令字

通信层命令 ID（1 字节）。带 `*` 的为 **OTA 相关，本仓库不实现**。

| 命令 | 值 | 用途 |
| --- | --- | --- |
| `CMD_DATA` | `0x01` | 数据通道 |
| `CMD_GET_TARGET_FEATURE_MAP` | `0x02` | 获取目标特性表 |
| `CMD_GET_TARGET_INFO` | `0x03` | 获取目标信息 |
| `CMD_DISCONNECT_CLASSIC_BLUETOOTH` | `0x06` | 断开经典蓝牙 |
| `CMD_SWITCH_DEVICE_REQUEST` | `0x0B` | 切换通信通道请求 |
| `CMD_ADV_DEVICE_NOTIFY` | `0xC2` | 广播设备通知（TWS） |
| `CMD_ADV_NOTIFY_SETTINGS` | `0xC3` | 广播设置通知（TWS） |
| `CMD_ADV_DEV_REQUEST_OPERATION` | `0xC4` | 广播设备请求操作（TWS） |
| `CMD_SETTINGS_COMMUNICATION_MTU` | `0xD1` | 设置通信 MTU |
| `CMD_GET_DEV_MD5` | `0xD4` | 获取设备 MD5 |
| `CMD_CUSTOM` | `0xF0` | 自定义（RCSP 命令载体） |
| `CMD_EXTRA_CUSTOM` | `0xFF` | 扩展自定义 |
| `CMD_OTA_GET_DEVICE_UPDATE_FILE_INFO_OFFSET` * | `0xE1` | OTA 查询文件偏移 |
| `CMD_OTA_INQUIRE_DEVICE_IF_CAN_UPDATE` * | `0xE2` | OTA 询问可升级性 |
| `CMD_OTA_ENTER_UPDATE_MODE` * | `0xE3` | 进入升级模式 |
| `CMD_OTA_EXIT_UPDATE_MODE` * | `0xE4` | 退出升级模式 |
| `CMD_OTA_SEND_FIRMWARE_UPDATE_BLOCK` * | `0xE5` | 传输固件块 |
| `CMD_OTA_GET_DEVICE_REFRESH_FIRMWARE_STATUS` * | `0xE6` | 查询刷新状态 |
| `CMD_REBOOT_DEVICE` * | `0xE7` | 重启设备 |
| `CMD_OTA_NOTIFY_UPDATE_CONTENT_SIZE` * | `0xE8` | 通知升级内容大小 |

## 2. 传输层参数

| 参数 | 值 | 说明 |
| --- | --- | --- |
| `BLE_MTU_MIN` | 20 | 最小 MTU |
| `BLE_MTU_MAX` | 509 | 最大 MTU |
| `CONNECT_TIMEOUT` | 40000 ms | 连接超时 |
| `DEFAULT_SCAN_TIMEOUT` | 8000 ms | 默认扫描超时 |
| `DEFAULT_SEND_CMD_TIMEOUT` | 3000 ms | 单条命令超时 |
| `SEND_DATA_MAX_TIMEOUT` | 6000 ms | 数据发送最大超时 |
| `RECEIVE_OTA_CMD_TIMEOUT` | 20000 ms | OTA 命令接收超时 |
| `PHY_LE_1M_MASK` | 1 | PHY 1M |
| `PHY_LE_2M_MASK` | 2 | PHY 2M |
| `PHY_LE_CODED_MASK` | 4 | PHY Coded |

## 3. 传输协议类型

| 值 | 名称 |
| --- | --- |
| 0 | `PROTOCOL_TYPE_BLE` |
| 1 | `PROTOCOL_TYPE_SPP` |
| 2 | `PROTOCOL_TYPE_GATT_OVER_BR_EDR` |

## 4. 扫描类型

| 值 | 名称 |
| --- | --- |
| 0 | `SCAN_TYPE_BLE` |
| 1 | `SCAN_TYPE_CLASSIC` |
| 2 | `SCAN_TYPE_ALL` |
