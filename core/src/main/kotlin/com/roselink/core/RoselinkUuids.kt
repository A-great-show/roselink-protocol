package com.roselink.core

import java.util.UUID

/**
 * RoseLink 耳机的 GATT 通道常量。
 *
 * 服务 / 写 / 通知三个特征即 RCSP 的 BLE 承载。
 */
object RoselinkUuids {

    /** 服务。 */
    val SERVICE: UUID = UUID.fromString("0000ae00-0000-1000-8000-00805f9b34fb")

    /** 写特征。 */
    val WRITE: UUID = UUID.fromString("0000ae01-0000-1000-8000-00805f9b34fb")

    /** 通知特征。 */
    val NOTIFY: UUID = UUID.fromString("0000ae02-0000-1000-8000-00805f9b34fb")

    /** 客户端特征配置描述符（订阅开关）。 */
    val CCCD: UUID = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
}
