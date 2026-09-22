# RoseLink 耳机 BLE / RCSP 协议

本文档描述 App 与耳机之间的 BLE 通信方式，用于编写自己的客户端。

## 1. GATT 通道

蓝牙低功耗（BLE）承载，服务与特征如下：

| 角色 | UUID |
| --- | --- |
| 服务 (Service) | `0000ae00-0000-1000-8000-00805f9b34fb` |
| 写入 (Write) | `0000ae01-0000-1000-8000-00805f9b34fb` |
| 通知 (Notify) | `0000ae02-0000-1000-8000-00805f9b34fb` |
| CCCD | `00002902-0000-1000-8000-00805f9b34fb` |

这是杰理（JieLi）RCSP 的 BLE 通道。设备同时可能具备传统蓝牙角色：

| 角色 | UUID |
| --- | --- |
| HFP | `0000111e-0000-1000-8000-00805f9b34fb` |
| A2DP | `0000110b-0000-1000-8000-00805f9b34fb` |
| SPP | `00001101-0000-1000-8000-00805f9b34fb` |

## 2. 连接流程

1. `startScan`：可按服务 UUID `ae00` 过滤，也可全量扫描。
2. `connectGatt(..., TRANSPORT_LE)` 连接。
3. `discoverServices()`，取 `ae01`（写）与 `ae02`（通知）。
4. 请求 MTU（协议允许范围 **20–509**，常用 247）。
5. 订阅 `ae02`（写 CCCD = `0100`），就绪后即可收发。

## 3. 配置帧格式

App 侧对设备上报的数据使用如下结构（`BluetoothFrameParser`）：

```
+-----------+---------------+------------------------------------------+
| dataType  | configCount   | 配置项 * configCount                     |
| 1 byte    | 1 byte        | type(1B) + length(1B) + value(length B)  |
+-----------+---------------+------------------------------------------+
```

- `dataType`：帧类型（当前实现只打印日志，未做分支）。
- `configCount`：后续配置项数量。
- 每个配置项：`type`（1 字节）、`length`（1 字节）、`value`（`length` 字节）。
- **多字节整数一律小端（LE）**。

示例（Hex）：

```
01 02  36 01 05  40 02 80 3E
|  |   |  |  |    |  |  \___/
|  |   |  |  |    |  |   sampleRate = 0x3E80 = 16000 (LE)
|  |   |  |  |    |  \ length = 2
|  |   |  |  |    \ type = 0x40 (录音采样率)
|  |   |  |  \ value = 0x05 (录音编码)
|  |   |  \ length = 1
|  |   \ type = 0x36 (录音编码)
|  \ configCount = 2
\ dataType = 0x01
```

## 4. 配置项类型表

由 `FieldProcessorRegistry` 注册的类型 → 字段映射。长度列是**期望字节数**；`bool` 为 1 字节非零即真。

### 文本

| type | 字段 | 长度 | 说明 |
| --- | --- | --- | --- |
| `0x00` | text.encoding | 1 | 文本编码枚举 |
| `0x01` | text.jokesCount | 2 | 笑话数量 |
| `0x34` | text.newsCount | 2 | 新闻数量 |
| `0x35` | text.textSyncLowLatency | 2 | 文本同步低延迟 |

### 录音（audioRecord）

| type | 字段 | 长度 | 说明 |
| --- | --- | --- | --- |
| `0x36` | encoding | 1 | 音频编码枚举 |
| `0x40` | sampleRate | 2 | 采样率；**< 8000 时强制为 16000** |
| `0x41` | channels | 1 | 声道；非 2 一律视为 1 |
| `0x42` | pcmFrameSize | 2 | PCM 帧大小 |
| `0x43` | opusFrameSize | 2 | OPUS 帧大小 |

### 播放（audioPlay）

| type | 字段 | 长度 | 说明 |
| --- | --- | --- | --- |
| `0x44` | encoding | 1 | 音频编码枚举 |
| `0x45` | sampleRate | 2 | 采样率 |
| `0x21` | channels | 1 | 声道；非 2 一律视为 1 |
| `0x46` | pcmFrameSize | 2 | PCM 帧大小 |
| `0x02` | opusFrameSize | 2 | OPUS 帧大小 |
| `0x03` | supportA2dp | 2 | 0 = 不支持，其余 = 支持 |

### 壁纸（wallpaper）

| type | 字段 | 长度 | 说明 |
| --- | --- | --- | --- |
| `0x10` | encoding | 1 | 图片编码枚举 |
| `0x11` | needPreview | 1 | 是否需要预览 |
| `0x12` | maxStoreCount | 2 | 最大存储数 |
| `0x13` | originalWidth | 2 | 原图宽 |
| `0x14` | originalHeight | 2 | 原图高 |
| `0x20` | previewWidth | 2 | 预览宽 |
| `0x22` | previewHeight | 2 | 预览高 |

### AI 图片（aiImage）

| type | 字段 | 长度 | 说明 |
| --- | --- | --- | --- |
| `0x23` | encoding | 1 | 图片编码枚举 |
| `0x24` | needPreview | 1 | 是否需要预览 |
| `0x25` | maxStoreCount | 2 | 最大存储数 |
| `0x30` | originalWidth | 2 | 原图宽 |
| `0x31` | originalHeight | 2 | 原图高 |
| `0x32` | previewWidth | 2 | 预览宽 |
| `0x33` | previewHeight | 2 | 预览高 |

> 未注册的类型：实现里直接 `skip(length)` 跳过，因此解析不会因新增类型而失败。

## 5. 未确认项（需要抓包）

以下内容在客户端侧（Dart）组装，本次未从二进制中还原，实现时请用 `btsnoop` 抓包核对：

1. **请求配置的报文**：`RoselinkHeadsetClient.requestDeviceConfig()` 目前仅作占位。
2. **文本 / 音频 / 图片「编码枚举」的具体取值**（`0x00`、`0x36`、`0x10`、`0x23` 等字段的数值含义）。
3. **播放控制 / 音量 / EQ** 等 RCSP 子命令的载荷格式。
4. `dataType` 的取值语义。
