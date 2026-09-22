package com.roselink.core

/**
 * 把配置帧里的每一项写入 [DeviceConfig]。
 *
 * 多字节整数为小端（LE）。未登记的类型按协议规范忽略。
 */
object DeviceConfigParser {

    /** 采样率下限，低于该值会被修正为 [DEFAULT_SAMPLE_RATE]。 */
    private const val MIN_SAMPLE_RATE = 8000
    private const val DEFAULT_SAMPLE_RATE = 16000

    fun parse(frame: ConfigFrame): DeviceConfig = DeviceConfig().also { config ->
        frame.items.forEach { apply(config, it) }
    }

    fun apply(config: DeviceConfig, item: ConfigItem) {
        val v = item.value
        when (item.type) {
            // ---- 文本 ----
            0x00 -> config.text.encoding = u8(v)
            0x01 -> config.text.jokesCount = u16(v)
            0x34 -> config.text.newsCount = u16(v)
            0x35 -> config.text.textSyncLowLatency = u16(v)

            // ---- 录音 ----
            0x36 -> config.audioRecord.encoding = u8(v)
            0x40 -> config.audioRecord.sampleRate =
                u16(v).let { if (it < MIN_SAMPLE_RATE) DEFAULT_SAMPLE_RATE else it }
            0x41 -> config.audioRecord.channels = u8(v).let { if (it == 2) 2 else 1 }
            0x42 -> config.audioRecord.pcmFrameSize = u16(v)
            0x43 -> config.audioRecord.opusFrameSize = u16(v)

            // ---- 播放 ----
            0x44 -> config.audioPlay.encoding = u8(v)
            0x45 -> config.audioPlay.sampleRate = u16(v)
            0x21 -> config.audioPlay.channels = u8(v).let { if (it == 2) 2 else 1 }
            0x46 -> config.audioPlay.pcmFrameSize = u16(v)
            0x02 -> config.audioPlay.opusFrameSize = u16(v)
            0x03 -> config.audioPlay.supportA2dp = if (u16(v) == 0) 0 else 1

            // ---- 壁纸 ----
            0x10 -> config.wallpaper.encoding = u8(v)
            0x11 -> config.wallpaper.needPreview = u8(v) != 0
            0x12 -> config.wallpaper.maxStoreCount = u16(v)
            0x13 -> config.wallpaper.originalWidth = u16(v)
            0x14 -> config.wallpaper.originalHeight = u16(v)
            0x20 -> config.wallpaper.previewWidth = u16(v)
            0x22 -> config.wallpaper.previewHeight = u16(v)

            // ---- AI 图片 ----
            0x23 -> config.aiImage.encoding = u8(v)
            0x24 -> config.aiImage.needPreview = u8(v) != 0
            0x25 -> config.aiImage.maxStoreCount = u16(v)
            0x30 -> config.aiImage.originalWidth = u16(v)
            0x31 -> config.aiImage.originalHeight = u16(v)
            0x32 -> config.aiImage.previewWidth = u16(v)
            0x33 -> config.aiImage.previewHeight = u16(v)
        }
    }

    private fun u8(v: ByteArray): Int {
        require(v.size >= 1) { "需要 1 字节，实际 ${v.size}" }
        return v[0].toInt() and 0xFF
    }

    private fun u16(v: ByteArray): Int {
        require(v.size >= 2) { "需要 2 字节，实际 ${v.size}" }
        return (v[0].toInt() and 0xFF) or ((v[1].toInt() and 0xFF) shl 8)
    }
}
