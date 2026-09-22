# roselink-protocol

RoseLink（`cn.ikaile.ruoshui.client`）耳机的 **BLE / 杰理（JieLi）RCSP** 控制协议整理，以及一个**原创、去 UI 的最小控制核心**（Android / Kotlin）。

目标：用一个自己写的界面控制耳机，只保留「扫描 → 连接 → 收发配置帧 → 切换模式」这条主链路，丢掉原 App 的臃肿逻辑与 UI。

## ⚠️ 声明

- 本仓库**不包含**任何官方 APK 的反编译产物（smali / Dart / so 反汇编）。所有代码均依据可观测的协议行为**独立重写**，用于互操作性研究。
- 协议常量（GATT UUID、帧结构、命令字）属于设备互操作所需的事实性信息。
- 请自行确认在你所在地区对自有设备做互操作 / 逆向的合法性；不得用于绕过授权、破解固件或侵犯他人权利。

## 耳机能力一览

用 `mt_apk_search` 在 `lib/arm64-v8a/libapp.so` 的字符串表里核过，App 具备以下能力：

| 能力 | 状态 | 证据 |
| --- | --- | --- |
| 降噪：普通 / 深度 / 自适应 | ✅ 有 | `normalAnc`、`deepAnc`、`setAdaptiveAnc` |
| 降噪强度分档 | ✅ 有 | `noiseAncLevel`、`setAncLevel`、`noise-anc-level-2-label` |
| 自动降噪 / 动态自动降噪 | ✅ 有 | `setAncAutoOn`、`setAncAutoDynamicOn` |
| 单耳降噪 | ✅ 有 | `device-personalization-single-ear-anc-subtitle` |
| 通透模式 + 3 种预设 | ✅ 有 | `Standard/Comfort/Voice Transparency` |
| 通透强度调节 | ✅ 有 | `TRANSPARENCY_LEVEL`、`getAncTransLevel` |
| 均衡器 EQ | ✅ 有 | `Equalizer`、`custom-eq-equalizer` |
| 触控手势自定义 | ✅ 有 | `rsCommonV2TouchListWithoutAnc` |
| OTA 固件升级 | ❌ 不实现 | `jl_bt_ota` SDK |

细节见 [`docs/features.md`](docs/features.md)。

## 结构

| 路径 | 内容 |
| --- | --- |
| `docs/protocol.md` | BLE 通道、连接流程、配置帧格式、配置项类型表（ 29 项） |
| `docs/rcsp-commands.md` | 命令字、传输层参数（MTU / 超时 / PHY） |
| `docs/features.md` | 降噪 / 通透 / EQ / 触控能力清单与抓包清单 |
| `core/.../RoselinkUuids.kt` | GATT UUID 常量 |
| `core/.../ConfigFrame.kt` | 配置帧数据模型 |
| `core/.../ConfigFrameCodec.kt` | 帧解析 / 生成 |
| `core/.../DeviceConfig.kt` | 设备配置模型 |
| `core/.../DeviceConfigParser.kt` | 类型 → 字段映射（含小端规则） |
| `core/.../NoiseControl.kt` | 降噪 / 通透模式模型 + 自定义命令组装 |
| `core/.../RcspCommands.kt` | 命令字常量 |
| `core/.../RoselinkHeadsetClient.kt` | GATT 客户端（扫描 / 连接 / 通知 / 写入 / MTU） |

## 保留 / 不包含

- ✅ 保留：扫描、连接、断开、MTU 协商、通知订阅、配置帧解析与生成、设备信息查询、噪声控制、EQ、触控设置
- ❌ 不包含：OTA 固件升级（`0xE1`–`0xE8`）
- ⏳ 待补：上述控制命令的**具体字节载荷**（需抓包，见 `docs/features.md` 第 6 节）

## 用法

```kotlin
val client = RoselinkHeadsetClient(context)

// 1. 设备列表
client.onDeviceFound = { device, rssi -> /* 你的列表 UI */ }
client.startScan()

// 2. 用户点击后连接
client.connect(device)

// 3. 设备配置（录音 / 播放 / 壁纸等）
client.onDeviceConfig = { cfg -> /* 你的设置页 UI */ }

// 4. 降噪切换（载荷待抓包确认）
client.write(CustomCommands.build(subCommand = TODO_SUB, body = byteArrayOf(1)))
```

## 后续

- [ ] 新 UI（另行设计，核心与 UI 已解耦）
- [ ] 按 `docs/features.md` 抓包补全降噪 / 通透 / EQ 载荷
