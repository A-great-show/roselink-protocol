# 耳机功能清单（除 OTA）

> **范围**：只保留与**耳机本体**相关的功能。
> 无线麦克风、AI 联网服务、App 外观个性化均已排除，见文末「已排除」。

## 0. 能力键（`control.*`）

客户端用一套 `control.xxx` 字符串作为**能力键**，判断设备支不支持某项设置、决定是否显示对应入口。

与耳机相关的共 **22 个**：

| 能力键 | 含义 |
| --- | --- |
| `control.deepAnc` | 深度降噪 |
| `control.ancAuto` | 自动降噪 |
| `control.ancAutoDynamic` | 动态自适应降噪 |
| `control.ancLine` | 场景降噪（通勤 / 室内等） |
| `control.eq` | 均衡器 |
| `control.eqBass` | 低音增强 |
| `control.spatialSwitch` | 空间音频开关 |
| `control.audioProtocol` | 音频协议（LDAC / LHDC） |
| `control.gameMode` | 游戏模式 / 低延迟 |
| `control.hearingProtection` | 听力保护 |
| `control.earDetection` | 入耳检测 |
| `control.earAdaptive` | 佩戴自适应 |
| `control.earTipFitTest` | 耳塞贴合度测试 |
| `control.earLanguage` | 耳机提示语言 |
| `control.touch` | 触控设置 |
| `control.tap` | 轻触 |
| `control.tapGesture` | 轻触手势 |
| `control.headMove` | 头部动作 |
| `control.findDevice` | 查找耳机 |
| `control.mulDevice` | 多点 / 多设备连接 |
| `control.dingLevel` | 提示音音量 |
| `control.gainLevel` | 增益 |

> 新客户端**必须保留同样的能力门控**，否则会在不支持的机型上显示无效开关。

---

## 1. 连接与设备管理

| 功能 | 标识 |
| --- | --- |
| 扫描 / 连接 / 断开 | BLE `AE00/AE01/AE02`、SPP、GATT over BR-EDR |
| 自动重连 / 后台保活 | `KeepAliveManager$KeepAliveService` |
| 设备重命名 | `deviceName`、`setDeviceName`、`_buildDeviceName` |
| **多点 / 多设备连接** | `setMulDevice`、`setAudioMulDevice`、`getMulDeviceNameList`、`mulDeviceMode` |
| 解除绑定 | "After deletion, the device will clear data…" |

## 2. 电量与设备信息

| 功能 | 标识 |
| --- | --- |
| 电量：左耳 / 右耳 / 充电仓 | `batteryLeft`、`batteryCaseInfo`、`RBatteryList`、`_handleBatteryData` |
| 低电量提醒 | `bindLowBatteryReminder`、`_scheduleLowBatteryReminder` |
| 固件版本 | `FIRMWARE_VERSION`、`extendedFirmwareVersion` |
| MAC 地址 | `device-personalization-mac-address` |
| CMIIT ID（中国无线电核准） | `device-personalization-cmiit-id` |

## 3. 降噪与通透

| 功能 | 标识 |
| --- | --- |
| 普通降噪 | `normalAnc` |
| 深度降噪 | `deepAnc`、`control.deepAnc`、`setDeepAncOn` |
| 自适应降噪 | `setAdaptiveAnc`、`Adaptive Noise Cancelling` |
| 自动 / 动态自动降噪 | `setAncAutoOn`、`setAncAutoDynamicOn` |
| 场景降噪 | `DeviceAncLineIntent` |
| 降噪强度分档 | `noiseAncLevel`、`setAncLevel`、`noise-anc-level-2-label` |
| 单耳降噪 | `device-personalization-single-ear-anc` |
| 通透开关 | `transAnc`、`ancTransActivate`、`ancClose` |
| 通透 3 预设 | `Standard Transparency`、`Comfort Transparency`、`Voice Transparency` |
| 通透强度 | `TRANSPARENCY_LEVEL`、`getAncTransLevel` |
| 能力查询 | `getAncList`、`hasAncNoiseControl`、`isAncModel` |

> 具体载荷见 `NoiseControl.kt`。

## 4. 音质与音效

| 功能 | 标识 |
| --- | --- |
| 均衡器 EQ | `Equalizer`、`custom-eq-equalizer`、`control.eq` |
| 低音增强 | `control.eqBass` |
| 空间音频开关 | `setSpatialSwitch`、`setSpatialAudioSwitchOn/Off` |
| 空间音频 4 场景 | `setSpatialAudioMusicModeMusic/Movie/Game/TV` |
| 空间音频模式 | `spatial_mode_head`（头动）、`spatial_mode_fixed`（固定） |
| 游戏模式 | `setGameModeOn/Off`、`gameModeList` |
| **高清音频 LDAC / LHDC** | `setAudioLdac`、`setAudioLdacLhdc`、`ldacLhdc` |

> ⚠️ **LDAC/LHDC 有账号级授权**：`/app/activateLDAC/activate`、`getActivateByLoginUser`。
> 不是纯本地开关，自己写客户端时要么绕过，要么保留登录流程。

## 5. 安全

| 功能 | 标识 |
| --- | --- |
| 听力保护 | `setHearingProtection`、`DeviceHearingProtectionSlider` |
| 档位 | 关闭 / 75 / 80 / 85 / 90 / 95 dB |

## 6. 佩戴与交互

| 功能 | 标识 |
| --- | --- |
| 入耳检测 | `control.earDetection`、`_handleInEarDetection` |
| 佩戴自适应 | `control.earAdaptive` |
| 耳塞贴合度测试 | `startEarTipFitTest`、`NormalEarTipFitTestPage`、`recivedEarTipFitResult` |
| 触控 / 轻触手势 | `control.touch`、`control.tap`、`control.tapGesture`、`rsCommonV2TouchListWithoutAnc` |
| 头部动作 | `control.headMove` |
| 查找耳机 | `setFindDeviceLeft/Right/Both`、`openFindDevice` |
| 提示语言 | `control.earLanguage`、`Firmware Language` |
| 自动关机 | `setAutoPowerOff` |

## 7. 音量

| 功能 | 标识 |
| --- | --- |
| 主音量 | `setVolume`、`AudioVolumeUp/Down/Mute` |
| 提示音音量 | `control.dingLevel` |
| 增益 | `control.gainLevel` |

---

## 8. 抓包清单

所有设置类功能都通过 RCSP 自定义命令 `CMD_CUSTOM (0xF0)` 下发：

```
[0xF0] [subCommand] [body...]
```

抓 `btsnoop_hci.log`，只看 `AE01`（写）方向，逐项操作并 diff：

| # | 操作 | 目的 |
| --- | --- | --- |
| 1 | 关降噪 / 普通 / 深度 / 自适应 | 模式编码 |
| 2 | 降噪强度 1/2/3 档 | 强度字段位置 |
| 3 | 通透开 + 3 预设 + 强度 | 通透编码 |
| 4 | EQ 切几个预设 | EQ 列表与格式 |
| 5 | 空间音频开 + 切 4 种模式 | 空间音频编码 |
| 6 | 游戏模式开 / 关 | 开关编码 |
| 7 | 入耳检测开 / 关 | 开关编码 |
| 8 | 查找左 / 右 / 双耳 | 查找命令格式 |
| 9 | 关掉一条触控手势 | 手势表格式 |
| 10 | 听力保护切档位 | 档位编码 |

对照要点：切换类命令通常只差 1–2 字节，**用 diff 对比最快**。

---

## 9. 已排除（App 有，但不属于耳机功能）

### 9.1 无线麦克风产品线

设备角色包含 TX1 / TX2（发射器）、RX（接收器）、Dongle，配套功能：

- `control.mic.gain`、`control.mic.txNoise`、`control.mic.txLowCut`、`control.mic.txTone`
- `control.mic.txRecord`、`control.mic.timecode`、`control.mic.rxChannel`、`control.mic.switch.`
- `MicrophoneVolumeUp/Down/Mute`、`setAutoPowerOff`（mic 版）
- `TX1/TX2 Firmware Upgrade`、`RX Firmware Upgrade`、`Dongle Firmware Upgrade`、`BLE Chip Firmware Upgrade`

### 9.2 AI 联网服务（starburst SDK）

均需联网，与耳机控制无关：

ASR、文本/语音/会议翻译、文本摘要、文生图、语音克隆、语音聊天、播客、解题、TTS、语音助手、VAD、文件任务轮询、GUI 下发。

### 9.3 App 外观个性化

`device-personalization-popup-theme-*` 全套（自定义主题/壁纸）、图片上传、连接弹窗、悬浮窗、通知栏电量、电池优化引导。

### 9.4 其他

OTA 固件升级（`0xE1`–`0xE8`）、极光/EngageLab 推送、字节 APM 统计、微信/支付宝 SDK。

## 10. 尚未确认

- `control.gainLevel`：耳机输出增益还是麦克风增益，待定（当前保留）。
- `control.hideSearch`：纯 UI 开关，已排除。
- `Sleep` 仅出现一次，可能是主题名，非独立功能。
- 各能力的**字节级载荷**均需抓包确认。
