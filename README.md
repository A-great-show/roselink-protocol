# roselink-protocol

RoseLink（`cn.ikaile.ruoshui.client`）耳机的 **BLE / 杰理（JieLi）RCSP** 控制协议整理，以及一个**原创、去 UI 的最小控制核心**（Android / Kotlin）。

目标：用一个自己写的界面控制耳机，只保留「扫描 → 连接 → 收发配置帧」这条主链路，丢掉原 App 的臃肿逻辑与 UI。

## ⚠️ 声明

- 本仓库**不包含**任何官方 APK 的反编译产物（smali / Dart / so 反汇编）。所有代码均依据可观测的协议行为**独立重写**，用于互操作性研究。
- 协议常量（GATT UUID、帧结构、命令字）属于设备互操作所需的事实性信息。
- 请自行确认在你所在地区对自有设备做互操作 / 逆向的合法性；不得用于绕过授权、破解固件或侵犯他人权利。

## 结构

| 路径 | 内容 |
| --- | --- |
| `docs/protocol.md` | BLE 通道、连接流程、配置帧格式、配置项类型表 |
| `docs/rcsp-commands.md` | 命令字、传输层参数（MTU / 超时 / PHY） |
| `core/src/main/kotlin/com/roselink/core/RoselinkUuids.kt` | GATT UUID 常量 |
| `core/src/main/kotlin/com/roselink/core/ConfigFrame.kt` | 配置帧数据模型 |
| `core/src/main/kotlin/com/roselink/core/ConfigFrameCodec.kt` | 帧解析 / 生成 |
| `core/src/main/kotlin/com/roselink/core/DeviceConfig.kt` | 设备配置模型 |
| `core/src/main/kotlin/com/roselink/core/DeviceConfigParser.kt` | 类型 → 字段映射（含小端规则） |
| `core/src/main/kotlin/com/roselink/core/RcspCommands.kt` | 命令字常量 |
| `core/src/main/kotlin/com/roselink/core/RoselinkHeadsetClient.kt` | GATT 客户端（扫描 / 连接 / 通知 / 写入 / MTU） |

## 保留 / 不包含

- ✅ 保留：扫描、连接、断开、MTU 协商、通知订阅、配置帧解析与生成、设备信息查询
- ❌ 不包含：OTA 固件升级（`0xE1`–`0xE8`）
- ⏳ 待补：EQ / 音量 / 播放控制等 RCSP 子命令的**具体载荷**（需抓包确认，见 `docs/protocol.md` 的「未确认项」）

## 用法

```kotlin
val client = RoselinkHeadsetClient(context)
client.onDeviceFound = { device, rssi -> /* 列表展示 */ }
client.onDeviceConfig = { cfg -> /* 用新 UI 渲染 */ }
client.startScan()
// 用户点击后
client.connect(device)
```

## 后续

- [ ] 新 UI（另行设计，核心与 UI 已解耦）
- [ ] 补齐 RCSP 子命令
