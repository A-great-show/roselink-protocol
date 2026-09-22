# 功能清单：降噪 / 通透 / 模式切换 / EQ

**结论：都有。** 这些能力由耳机固件提供，App 只做「读能力 → 展示 → 下发切换」。

⚠️ 全部逻辑位于 `lib/arm64-v8a/libapp.so`（Dart AOT），因此下面只能给出**功能清单与标识证据**；字节级载荷必须抓包才能确定。

---

## 1. 降噪（ANC）

| 能力 | 客户端可见标识 |
| --- | --- |
| 普通降噪 | `normalAnc`、`noise.simpleAnc` |
| 深度降噪 | `deepAnc`、`control.deepAnc`、`setDeepAncOn`、`get:setDeepAncOn`、`DeviceDeepAncIntent` |
| 自适应降噪 | `setAdaptiveAnc`、`Adaptive Noise Cancelling`、`DeviceAncAutoDynamicIntent` |
| 降噪强度分档 | `noiseAncLevel`、`setAncLevel`、`noise-anc-level-2-label` |
| 能力查询（列表） | `getAncList`、`get:getAncList`、`_handleAncList@…` |
| 是否支持降噪 | `get:hasAncNoiseControl`、`isAncModel` |
| 自动降噪 | `setAncAutoOn`、`setAncAutoDynamicOn`、`get:setAncAutoOn`、`get:setAncAutoDynamicOn` |
| 场景化（线路 / 通勤） | `DeviceAncLineIntent`、`DeviceAncAutoIntent` |
| 单耳降噪 | `_isAllowSingleEarAncEnabledForCurrentDevice@…`、`device-personalization-single-ear-anc-subtitle` |
| 面板 UI 组件 | `DeviceAncAutoNoiseAncPanelItemWidgetModel`、`DeviceLimitNoiseAncPanelItemWidgetModel`、`DeviceAutoNoiseAncDynamicPanelItemWidgetModel` |
| 图标资源 | `assets/images/icons/common/anc_normal_activate.svg`、`furinaAncIconActive`、`RThemeComponentOfAnc` |
| 其他 | `ANC_DATA`、`ancClose`、`get:setNoiseModeAnc` |

## 2. 通透模式（Transparency）

| 能力 | 客户端可见标识 |
| --- | --- |
| 通透开关 | `transAnc`、`ancTransActivate`、`transparency` |
| 预设：标准 / 舒适 / 人声 | `Standard Transparency`、`Comfort Transparency`、`Voice Transparency` |
| 通透强度 | `TRANSPARENCY_LEVEL`、`Transparency Level`、`getAncTransLevel`、`get:getAncTransLevel`、`_handleTransparencyLevel@…` |
| 调节文案 | `Transparency Effect Adjustment`、`Control the intensity of the current transparency effect` |
| 状态同步 | `transparencyMask` |

## 3. 均衡器（EQ）

| 能力 | 客户端可见标识 |
| --- | --- |
| 均衡器 | `Equalizer` |
| 自定义 EQ | `custom-eq-equalizer` |

> 推测还有预设 EQ 列表（流行 / 低音增强等），需抓包确认。

## 4. 触控手势

- `rsCommonV2TouchListWithoutAnc`：触控自定义列表，存在「含 ANC 手势」与「不含 ANC 手势」两套。

## 5. 下发通道

上述设置走 **RCSP 自定义命令**，即命令字 `CMD_CUSTOM (0xF0)`：

```
[0xF0] [subCommand] [body...]
```

`subCommand` 与 `body` 均在与 Dart 侧组装，静态分析拿不到数值。

## 6. 抓包清单（按这个顺序做就能补全）

同步抓 `btsnoop_hci.log`，只看 `AE01`（写）方向：

| # | 操作 | 要确定什么 |
| --- | --- | --- |
| 1 | 关闭降噪 | 基线报文（可能是最简的） |
| 2 | 普通降噪 | `subCommand` 是否变化 |
| 3 | 深度降噪开 | 深度档位的编码 |
| 4 | 自适应降噪开 | 自适应与深度的差异 |
| 5 | 降噪强度 1/2/3 档来回切 | 强度字段的字节位置 |
| 6 | 通透模式开 | 通透与降噪是否同一个 `subCommand` |
| 7 | 通透预设 标准/舒适/人声 | 预设字段位置 |
| 8 | 通透强度滑动 | 强度字段与降噪强度是否复用 |
| 9 | 单耳降噪开关 | 独立命令还是模式参数 |
| 10 | 自动降噪 / 动态自动降噪 | 开关位与组合关系 |

### 对照要点

1. `AE01` 是写入（发），`AE02` 是通知（收），别混。
2. 切换类命令通常只差 1–2 个字节，**用 diff 对比最快**。
3. 注意区分「命令」与「状态回传」：App 切换后设备会回一条状态帧。
4. 记下 `subCommand` 是否稳定：如果与操作无关，说明它在 `body` 里编码。

### 填到哪里

把确认后的 `subCommand` 与 `body` 填进 `core/src/main/kotlin/com/roselink/core/NoiseControl.kt` 的 `CustomCommands` 调用处。
