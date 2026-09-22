package com.roselink.core

/**
 * 配置帧编解码。
 *
 * 线格式：`dataType(1B) | configCount(1B) | [ type(1B) | length(1B) | value(length B) ] *`
 */
object ConfigFrameCodec {

    private const val HEADER_SIZE = 2
    private const val ITEM_HEADER_SIZE = 2

    /** 解析一帧。数据不足时抛出 [IllegalArgumentException]。 */
    fun parse(data: ByteArray): ConfigFrame {
        var offset = 0

        fun nextU8(what: String): Int {
            require(offset < data.size) { "$what 缺失，帧长度 ${data.size}" }
            return data[offset++].toInt() and 0xFF
        }

        val dataType = nextU8("dataType")
        val count = nextU8("configCount")

        val items = ArrayList<ConfigItem>(count)
        repeat(count) {
            val index = it
            val type = nextU8("第 $index 项的 type")
            val length = nextU8("第 $index 项的 length")
            require(offset + length <= data.size) {
                "第 $index 项 (type=0x${type.toString(16)}) 声明 $length 字节，剩余 ${data.size - offset} 字节"
            }
            val value = data.copyOfRange(offset, offset + length)
            offset += length
            items += ConfigItem(type, value)
        }

        return ConfigFrame(dataType, items)
    }

    /** 生成一帧。 */
    fun build(frame: ConfigFrame): ByteArray {
        require(frame.dataType in 0..0xFF) { "dataType 越界: ${frame.dataType}" }
        require(frame.items.size <= 0xFF) { "配置项过多: ${frame.items.size}" }

        var size = HEADER_SIZE
        frame.items.forEach { size += ITEM_HEADER_SIZE + it.value.size }

        val out = ByteArray(size)
        var offset = 0
        out[offset++] = frame.dataType.toByte()
        out[offset++] = frame.items.size.toByte()

        frame.items.forEach { item ->
            require(item.type in 0..0xFF) { "type 越界: ${item.type}" }
            require(item.value.size <= 0xFF) { "value 过长: ${item.value.size}" }
            out[offset++] = item.type.toByte()
            out[offset++] = item.value.size.toByte()
            item.value.copyInto(out, offset)
            offset += item.value.size
        }

        return out
    }
}
