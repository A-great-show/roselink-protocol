package com.roselink.core

/** 文本相关配置。 */
data class TextConfig(
    /** 对应 type 0x00。 */
    var encoding: Int = 0,
    /** 对应 type 0x01。 */
    var jokesCount: Int = 0,
    /** 对应 type 0x34。 */
    var newsCount: Int = 0,
    /** 对应 type 0x35。 */
    var textSyncLowLatency: Int = 0,
)

/** 录音配置。 */
data class RecordConfig(
    /** 对应 type 0x36。 */
    var encoding: Int = 0,
    /** 对应 type 0x40。 */
    var sampleRate: Int = 0,
    /** 对应 type 0x41。 */
    var channels: Int = 1,
    /** 对应 type 0x42。 */
    var pcmFrameSize: Int = 0,
    /** 对应 type 0x43。 */
    var opusFrameSize: Int = 0,
)

/** 播放配置。 */
data class PlayConfig(
    /** 对应 type 0x44。 */
    var encoding: Int = 0,
    /** 对应 type 0x45。 */
    var sampleRate: Int = 0,
    /** 对应 type 0x21。 */
    var channels: Int = 1,
    /** 对应 type 0x46。 */
    var pcmFrameSize: Int = 0,
    /** 对应 type 0x02。 */
    var opusFrameSize: Int = 0,
    /** 对应 type 0x03。0 = 不支持，其余 = 支持。 */
    var supportA2dp: Int = 1,
)

/** 图片配置（壁纸与 AI 图片共用结构）。 */
data class ImageConfig(
    /** 对应 type 0x10 / 0x23。 */
    var encoding: Int = 0,
    /** 对应 type 0x11 / 0x24。 */
    var needPreview: Boolean = false,
    /** 对应 type 0x12 / 0x25。 */
    var maxStoreCount: Int = 0,
    /** 对应 type 0x13 / 0x30。 */
    var originalWidth: Int = 0,
    /** 对应 type 0x14 / 0x31。 */
    var originalHeight: Int = 0,
    /** 对应 type 0x20 / 0x32。 */
    var previewWidth: Int = 0,
    /** 对应 type 0x22 / 0x33。 */
    var previewHeight: Int = 0,
)

/** 耳机上报的完整设备配置。 */
data class DeviceConfig(
    val text: TextConfig = TextConfig(),
    val audioRecord: RecordConfig = RecordConfig(),
    val audioPlay: PlayConfig = PlayConfig(),
    val wallpaper: ImageConfig = ImageConfig(),
    val aiImage: ImageConfig = ImageConfig(),
)
