# 功能清单（除 OTA 外全部）

本文档是对 RoseLink App **除 OTA 固件升级外的全部功能**的盘点，用于确认新客户端要保留哪些能力。

## 0. 判定方法

客户端用一套 `control.xxx` 字符串作为**能力键**，用来判断设备支不支持某项设置、决定是否显示对应入口。

只要把 `control.` 前缀扫一遍，就能把功能面完整列出。下面第 1 节就是从 `lib/arm64-v8a/libapp.so` 字符串表提取的全部 31 个能力键。

> 对新客户端的启示：**必须保留同样的能力门控逻辑**，否则会在不支持的设备上显示无效开关。

## 1. 全部能力键（`control.*`）

| 能力键 | 含义 |
| --- | --- |
| `control.deepAnc` | 深度降噪 |
| `control.ancAuto` | 自动降噪 |
| `control.ancAutoDynamic` | 动态自适应降噪 |
| `control.ancLine` | 场景 / 线路降噪 |
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
| `control.mulDevice` | 多设备连接 / 多点 |
| `control.gainLevel` | 增益 |
| `control.dingLevel` | 提示音音量 |
| `control.hideSearch` | 隐藏搜索入口（UI） |
| `control.mic.gain` | 麦克风增益 |
| `control.mic.txNoise` | 麦克风降噪 |
| `control.mic.txLowCut` | 麦克风低切 |
| `control.mic.txTone` | 麦克风音色 |
| `control.mic.txRecord` | 麦克风录音 |
| `control.mic.timecode` | 麦克风时间码 |
| `control.mic.rxChannel` | 麦克风接收声道 |
| `control.mic.switch.` | 麦克风开关 |

## 2. 连接与设备管理

| 功能 | 证据 |
| --- | --- |
| 扫描 / 连接 / 断开（BLE / SPP / GATT over BR-EDR） | `PROTOCOL_TYPE_*`、`UUID_SERVICE/WRITE/NOTIFY/SPP` |
| 设备重命名 | `deviceName`、`_buildDeviceName` |
| **多设备 / 多点连接** | `setMulDevice`、`setAudioMulDevice`、`getMulDeviceNameList`、`DeviceMulDeviceIntent`、`mulDeviceMode` |
| 设备信息：MAC | `device-personalization-mac-address` |
| 设备信息：CMIIT ID（中国无线电核准） | `device-personalization-cmiit-id` |
| 固件版本号 | `FIRMWARE_VERSION`、`extendedFirmwareVersion`、`DONGLE_FIRMWARE_VERSION` |
| 解绑 / 删除设备 | "After deletion, the device will clear data…" |
| 后台保活 | `KeepAliveManager$KeepAliveService`（前台服务） |
| 电量：左耳 / 右耳 / 充电仓 | `batteryLeft`、`batteryCaseInfo`、`RBatteryList`、`_handleBatteryData` |
| 低电量提醒 | `bindLowBatteryReminder`、`_scheduleLowBatteryReminder` |
| 通知栏推送电量 | `Push Battery to Notification Shade` |

## 3. 降噪 / 通透与音频

### 3.1 降噪（ANC）

| 能力 | 标识 |
| --- | --- |
| 普通降噪 | `normalAnc`、`noise.simpleAnc` |
| 深度降噪 | `deepAnc`、`control.deepAnc`、`setDeepAncOn` |
| 自适应降噪 | `setAdaptiveAnc`、`Adaptive Noise Cancelling` |
| 自动 / 动态自动降噪 | `setAncAutoOn`、`setAncAutoDynamicOn` |
| 场景 / 线路降噪 | `DeviceAncLineIntent` |
| 降噪强度分档 | `noiseAncLevel`、`setAncLevel`、`noise-anc-level-2-label` |
| 单耳降噪 | `device-personalization-single-ear-anc`、`_isAllowSingleEarAncEnabledForCurrentDevice` |
| 能力查询 | `getAncList`、`hasAncNoiseControl`、`isAncModel` |

### 3.2 通透（Transparency）

| 能力 | 标识 |
| --- | --- |
| 通透开关 | `transAnc`、`ancTransActivate`、`ancClose` |
| 3 种预设 | `Standard Transparency`、`Comfort Transparency`、`Voice Transparency` |
| 通透强度 | `TRANSPARENCY_LEVEL`、`Transparency Level`、`getAncTransLevel` |

### 3.3 音质与音效

| 能力 | 标识 |
| --- | --- |
| 均衡器 EQ | `Equalizer`、`custom-eq-equalizer`、`control.eq` |
| 低音增强 | `control.eqBass` |
| **空间音频** | `setSpatialSwitch`、`setSpatialAudioMusicMode`、`spatialAudioMode` |
| 空间音频 4 种场景模式 | `setSpatialAudioMusicModeMusic/Movie/Game/TV` |
| 空间音频模式 | `spatial_mode_head`（头动）、`spatial_mode_fixed`（固定） |
| 游戏模式 / 低延迟 | `setGameModeOn`、`setGameModeOff`、`gameModeList` |
| **高清音频 LDAC / LHDC** | `setAudioLdac`、`setAudioLdacLhdc`、`ldacLhdc` |
| LDAC 需服务端激活 | `/app/activateLDAC/activate`、`getActivateByLoginUser` |
| 听力保护 | `setHearingProtection`、档位 75/80/85/90/95 dB、`hearing-protection-off` |
| 音量 | `setVolume`、`AudioVolumeUp/Down/Mute` |
| 提示音音量 | `control.dingLevel` |
| 增益 | `control.gainLevel` |

> LDAC/LHDC 有**账号级授权**（`/app/activateLDAC/getActivateByLoginUser`），不是纯本地开关。自己写客户端时要么绕过，要么保留登录流程。

## 4. 佩戴与交互

| 功能 | 标识 |
| --- | --- |
| 入耳检测 | `control.earDetection`、`_handleInEarDetection`、`labInEarMode` |
| 佩戴自适应 | `control.earAdaptive` |
| **耳塞贴合度测试** | `startEarTipFitTest`、`NormalEarTipFitTestPage`、`recivedEarTipFitResult` |
| 触控自定义 | `control.touch`、`control.tap`、`control.tapGesture`、`rsCommonV2TouchListWithoutAnc` |
| 头部动作 | `control.headMove` |
| **查找耳机** | `openFindDevice`、`setFindDeviceLeft/Right/Both`、`navigation.findDevice` |
| 耳机提示语言 | `control.earLanguage`、`Firmware Language` |
| 自动关机 | `setAutoPowerOff`、`micAutoPowerOffState` |

## 5. 麦克风（说明这是含无线麦 / 领夹麦的产品线）

设备角色包含 **TX1 / TX2（两个发射器）**、**RX（接收器）**、**Dongle**：

| 功能 | 标识 |
| --- | --- |
| 麦克风增益 | `control.mic.gain` |
| 麦克风降噪 | `control.mic.txNoise` |
| 麦克风低切 | `control.mic.txLowCut` |
| 麦克风音色 | `control.mic.txTone` |
| 麦克风录音 | `control.mic.txRecord` |
| 麦克风时间码 | `control.mic.timecode` |
| 接收声道 | `control.mic.rxChannel` |
| 麦克风静音 / 音量 | `MicrophoneVolumeMute/Up/Down` |
| 多设备角色固件 | `TX1 Firmware Upgrade`、`TX2 Firmware Upgrade`、`RX Firmware Upgrade`、`Dongle Firmware Upgrade` |

> 因此「控制耳机」实际是**多形态设备**：耳机、发射器、接收器、Dongle 各有不同设置项。新客户端需要按设备类型分别处理。

## 6. 个性化与外观

| 功能 | 标识 |
| --- | --- |
| 自定义主题（新建 / 保存 / 应用 / 删除 / 分享） | `device-personalization-popup-theme-*` 全套 |
| 图片上传（左 / 右 / 充电图） | `popup-upload`、`popup-left-image`、`popup-right-image`、`popup-charge-image` |
| 背景清除 | `popup-background-clear` |
| 悬浮窗权限 | `popup-overlay-permission`（配合 `SYSTEM_ALERT_WINDOW`） |
| 连接弹窗 | `device-personalization-connected-popup` |
| 通知栏电量 | `device-personalization-notification-battery` |
| 电池优化白名单引导 | `personalization-battery-optimization` |

## 7. AI 能力（starburst SDK，走网络）

| 模块 | 接口 |
| --- | --- |
| 语音识别 ASR | `SpeechProcessingManager`（43 方法） |
| 文本翻译 | `translateText`、`/ai/translate/doTranslate` |
| 语音翻译 / 双人对话 | `_handleSpeechTranslate`、`translateByDouble`、`leftTranslateByDouble` |
| 会议纪要翻译 | `record_ai_meeting_translate_switch` |
| 文本摘要 | `ITextSummaryCallback` |
| 文生图 / 图生图 | `ITextToImageManager`、`ImageToImageRequest` |
| 语音克隆 | `VoiceCloneManager` |
| 语音聊天 | `VoiceChatManager`（84 方法） |
| 播客 | `PodcastManager` |
| 解题 | `QuestionSolveManager` |
| TTS 朗读 | `ITextToSpeechStreamCallback` |
| 语音助手（全屏对话） | `StarburstVoiceAssistantController` |
| 语音活动检测 VAD | `VadConfig` |
| 文件任务 / 轮询 | `FileProcessingManager`、`FileResultTaskPoller` |
| GUI 下发（带屏设备） | `GuiControlManager`（45 方法） |

> 这整块 AI 能力都是**联网服务**，不属于“控制耳机”的核心链路。建议新客户端直接丢，除非你要保留翻译 / 会议功能。

## 8. 建议的最小保留集

如果你只想要“控制耳机的基础功能”，建议保留：

```
连接管理 + 电量 + 设备信息 + 降噪/通透 + EQ + 空间音频 + 游戏模式
+ 触控设置 + 入耳检测 + 查找耳机 + 贴合度测试 + 听力保护 + 多点连接
```

可丢：AI 全家桶、个性化主题/壁纸、悬浮窗、推送、广告/统计 SDK、OTA。

## 9. 抓包清单

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

## 10. 尚未确认

- `Sleep` 只出现一次，可能只是主题名，不像独立功能。
- `control.mic.timecode`（时间码）具体用途未明。
- 各能力的**字节级载荷**均需抓包确认。
