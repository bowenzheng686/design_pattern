# 第 13 章：策略 Strategy

## 场景

订单运费会按标准、加急、自提三种算法计算。`Order`（Context）只委派给 `ShippingStrategy`，调用方显式选择算法，消除散落的 `if/else`。

```powershell
.\scripts\run.ps1 13
```

示例对相同订单运行三种策略，验证算法可互换且订单数据不变。新增配送商只需新策略。策略由外部选择；状态模式则通常由 Context 内部状态转移驱动，这是二者意图上的关键区别。

