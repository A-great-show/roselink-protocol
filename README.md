# roselink-protocol

RoseLink（`cn.ikaile.ruoshui.client`）**耳机**的 BLE / 杰理（JieLi）RCSP 控制协议整理，以及一个**原创、去 UI 的最小控制核心**（Android / Kotlin）。

目标：用自己的界面控制耳机，只保留必要的控制链路，丢掉原 App 的臃肿逻辑与 UI。

## ⚠️ 声明

- 本仓库**不包含**任何官方 APK 的反编译产物（smali / Dart / so 反汇编）。所有代码均为依据可观测的协议行为**独立重写**，用于互操作性研究。
- 协议常量（GATT UUID、帧结构、命令字）属于设备互操作所需的事实性信息。
- 请自行确认在你所在地区对自有设备做互操作 / 逆向的合法性；不得用于绕过授权、破解固件或侵犯他人权利。

## 范围

**只做耳机。** 原 App 还支持无线麦克风（TX/RX/Dongle）、AI 联网服务（翻译 / 语音聊天等）、外观个性化——全部不在本仓库范围内。

## 耳机功能（除 OTA）

完整清单见 [`docs/features.md`](docs/features.md)（含 22 个耳机相关能力键）。摘要：

| 模块 | 内容 |
| --- | --- |
| 连接管理 | 扫描 / 连接 / 断开、自动重连、重命名、**多点连接** |
| 电量 | 左耳 / 右耳 / 充电仓、低电量提醒 |
| 设备信息 | 固件版本、MAC、CMIIT ID |
| 降噪 | 普通 / 深度 / 自适应 / 自动 / 场景 / 单耳 + 强度分档 |
| 通透 | 开关 + 标准 / 舒适 / 人声 3 预设 + 强度 |
| 音效 | EQ、低音增强、**空间音频**（4 场景 × 头动/固定）、游戏模式 |
| 高清音频 | LDAC / LHDC（⚠️ **需账号级激活**） |
| 安全 | 听力保护（75–95 dB） |
| 佩戴 | 入耳检测、佩戴自适应、耳塞贴合度测试 |
| 交互 | 触控 / 轻触手势、头部动作、查找耳机、提示语言、自动关机 |
| 音量 | 主音量、提示音音量、增益 |
| OTA | ❌ **不实现** |

> 客户端依赖 `control.*` 能力键做门控，新 UI 必须保留这套判断。

## 结构

| 路径 | 内容 |
| --- | --- |
| `docs/protocol.md` | BLE 通道、连接流程、配置帧格式、配置项类型表（ 29 项） |
| `docs/rcsp-commands.md` | 命令字、传输层参数（MTU / 超时 / PHY） |
| `docs/features.md` | 耳机功能清单 + 能力键表 + 抓包清单 + 已排除项 |
| `core/.../RoselinkUuids.kt` | GATT UUID 常量 |
| `core/.../ConfigFrame.kt` | 配置帧数据模型 |
| `core/.../ConfigFrameCodec.kt` | 帧解析 / 生成 |
| `core/.../DeviceConfig.kt` | 设备配置模型 |
| `core/.../DeviceConfigParser.kt` | 类型 → 字段映射（含小端规则） |
| `core/.../NoiseControl.kt` | 降噪 / 通透模式模型 + 自定义命令组装 |
| `core/.../RcspCommands.kt` | 命令字常量 |
| `core/.../RoselinkHeadsetClient.kt` | GATT 客户端（扫描 / 连接 / 通知 / 写入 / MTU） |

## 用法

```kotlin
val client = RoselinkHeadsetClient(context)

client.onDeviceFound = { device, rssi -> /* 设备列表 UI */ }
client.startScan()
client.connect(device)
client.onDeviceConfig = { cfg -> /* 设置页 UI */ }

// 噪声控制（载荷待抓包确认）
client.write(CustomCommands.setNoiseMode(NoiseMode.DEEP_ANC, level = 2))
```

## 后续

- [ ] 新 UI（另行设计，核心与 UI 已解耦）
- [ ] 按 `docs/features.md` 第 8 节抓包，补全各设置载荷
