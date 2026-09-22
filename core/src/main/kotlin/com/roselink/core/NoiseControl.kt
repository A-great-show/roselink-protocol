package com.roselink.core

/**
 * 噪声控制（降噪 / 通透）模型与自定义命令组装。
 *
 * ⚠️ 重要说明
 *
 * 本文件的枚举「名称」来自客户端可观测标识（见 docs/features.md），
 * 但对应的「字节值」尚未从二进制中还原：客户端把噪声控制做在 Dart 侧，
 * 通过 RCSP 自定义命令（CMD_CUSTOM = 0xF0）下发，静态分析只能拿到功能标识。
 *
 * 请按 docs/features.md 第 6 节的抓包清单补全编码（目前尚未标 TODO 的字段均属未确认）。
 */

/**
 * 噪声控制模式。
 *
 * 命名依据：`ancClose` / `normalAnc` / `deepAnc` / `setAdaptiveAnc` / `transAnc`。
 * 字节值待确认。
 */
enum class NoiseMode {
    /** 关闭噪声控制（`ancClose`）。 */
    OFF,

    /** 普通降噪（`normalAnc`、`noise.simpleAnc`）。 */
    NORMAL_ANC,

    /** 深度降噪（`deepAnc`、`control.deepAnc`）。 */
    DEEP_ANC,

    /** 自适应降噪（`setAdaptiveAnc`、`Adaptive Noise Cancelling`）。 */
    ADAPTIVE_ANC,

    /** 通透模式（`transAnc`、`transparency`）。 */
    TRANSPARENCY,
}

/**
 * 通透预设。命名依据 `Standard / Comfort / Voice Transparency`。
 */
enum class TransparencyPreset {
    /** 标准（`Standard Transparency`）。 */
    STANDARD,

    /** 舒适（`Comfort Transparency`）。 */
    COMFORT,

    /** 人声（`Voice Transparency`）。 */
    VOICE,
}

/**
 * 设备噪声控制状态。字段均为「已确认存在、编码待确认」。
 */
data class NoiseControlState(
    var mode: NoiseMode = NoiseMode.OFF,
    /** 降噪强度（`noiseAncLevel` / `setAncLevel`）。 */
    var ancLevel: Int = 0,
    /** 通透强度（`TRANSPARENCY_LEVEL` / `getAncTransLevel`）。 */
    var transparencyLevel: Int = 0,
    var transparencyPreset: TransparencyPreset = TransparencyPreset.STANDARD,
    /** 自适应降噪开关（`setAncAutoOn`）。 */
    var autoAnc: Boolean = false,
    /** 动态自适应降噪（`setAncAutoDynamicOn`）。 */
    var autoAncDynamic: Boolean = false,
    /** 深度降噪开关（`setDeepAncOn`）。 */
    var deepAncOn: Boolean = false,
    /** 单耳降噪（`device-personalization-single-ear-anc`）。 */
    var singleEarAnc: Boolean = false,
)

/**
 * RCSP 自定义命令组装。
 *
 * 结构为 `CMD_CUSTOM(0xF0) | subCommand | body...`。
 *
 * 本对象只保证「结构」正确；[subCommand] 与 [body] 的取值必须由抓包确定。
 * 不要因为能编译就当作协议已实现。
 */
object CustomCommands {

    /**
     * 噪声控制子命令占位值。
     *
     * TODO: 抓包确认后替换。它很可能是一个固定的 subCommand，
     * 而具体模式与强度编码在 body 里。
     */
    const val NOISE_CONTROL_SUB_COMMAND: Int = 0x00

    /**
     * 组装一条自定义命令。
     *
     * @param subCommand 子命令字节（见 [NOISE_CONTROL_SUB_COMMAND] 说明）。
     * @param body 命令体，编码待确认。
     */
    fun build(subCommand: Int, body: ByteArray): ByteArray =
        ByteArray(2 + body.size).also { out ->
            out[0] = RcspCommands.CUSTOM.toByte()
            out[1] = subCommand.toByte()
            body.copyInto(out, 2)
        }

    /**
     * 切换噪声控制模式。
     *
     * TODO: body 编码待抓包。已知客户端下发时会同时带上「模式」与「强度」两个维度。
     */
    fun setNoiseMode(mode: NoiseMode, level: Int = 0): ByteArray =
        build(NOISE_CONTROL_SUB_COMMAND, byteArrayOf(mode.ordinal.toByte(), level.toByte()))

    /**
     * 设置通透预设与强度。
     *
     * TODO: body 编码待抓包。
     */
    fun setTransparency(preset: TransparencyPreset, level: Int): ByteArray =
        build(
            NOISE_CONTROL_SUB_COMMAND,
            byteArrayOf(preset.ordinal.toByte(), level.toByte()),
        )

    /**
     * 自适应降噪开关。
     *
     * TODO: body 编码待抓包。
     */
    fun setAutoAnc(enabled: Boolean, dynamic: Boolean = false): ByteArray =
        build(
            NOISE_CONTROL_SUB_COMMAND,
            byteArrayOf(if (enabled) 1 else 0, if (dynamic) 1 else 0),
        )

    /**
     * 深度降噪开关。
     *
     * TODO: body 编码待抓包。
     */
    fun setDeepAnc(enabled: Boolean): ByteArray =
        build(NOISE_CONTROL_SUB_COMMAND, byteArrayOf(if (enabled) 1 else 0))

    /**
     * 单耳降噪开关。
     *
     * TODO: body 编码待抓包。
     */
    fun setSingleEarAnc(enabled: Boolean): ByteArray =
        build(NOISE_CONTROL_SUB_COMMAND, byteArrayOf(if (enabled) 1 else 0))
}
