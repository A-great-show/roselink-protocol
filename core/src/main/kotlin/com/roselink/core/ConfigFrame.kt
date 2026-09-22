package com.roselink.core

/** 单个配置项：类型 + 原始值。长度由 [value] 推导。 */
data class ConfigItem(
    val type: Int,
    val value: ByteArray,
) {
    val length: Int get() = value.size

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ConfigItem) return false
        return type == other.type && value.contentEquals(other.value)
    }

    override fun hashCode(): Int = 31 * type + value.contentHashCode()
}

/** 一帧设备配置数据。 */
data class ConfigFrame(
    val dataType: Int,
    val items: List<ConfigItem>,
)
