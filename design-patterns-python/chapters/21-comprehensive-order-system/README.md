# 第 21 章：综合实战——Python 可观测订单履约系统

## 场景与边界

前台提交订单后，系统需要校验客户和商品、执行 VIP 定价、适配旧支付 SDK、对瞬时故障重试、驱动订单状态、记录审计事件并通知客户。金额使用整数分，所有数据只存在于内存。

## 模式角色

| 模式 | Python 角色 | 变化点 |
|---|---|---|
| Builder | `OrderBuilder` | 分步构造与结构校验 |
| Strategy | `PricingStrategy` 可调用对象 | 定价算法 |
| Responsibility Chain | `ValidationHandler` | 校验规则/顺序 |
| Adapter | `LegacyPaymentAdapter` | SDK 协议翻译 |
| Proxy | `RetryingPaymentProxy` | 重试与调用观测 |
| Template Method | `CheckoutWorkflow.checkout` | 稳定履约骨架 |
| Command | 支付/发货命令 | 操作历史与逆序补偿 |
| State | `NewState/PaidState/ShippedState` | 合法状态转换 |
| Observer | `EventBus` | 一对多事件广播 |
| Bridge | `OrderNotification` + `MessageSender` | 内容和发送通道 |
| Facade | `OrderSystemFacade` | 单一客户端入口 |

## 运行

```powershell
python tools/run_chapter.py 21
python tools/run_chapter.py 21 --fail-payment
python tools/run_chapter.py 21 --invalid-order
```

- 正常：1 次支付、2 个事件、最终 `SHIPPED`；
- 故障恢复：第 1 次支付报瞬时错误，第 2 次恢复；
- 非法输入：责任链返回 `REJECTED`，支付/通知/审计次数均为 0。

三种结果都带自动断言和 PASS，因为后两种是明确的预期验收场景。

## 取舍

事件总线当前同步执行以保证教学顺序。生产异步化后必须增加幂等、背压、死信和 Outbox；命令恢复只回滚内存状态，真实外部退款应建模为幂等补偿命令。

