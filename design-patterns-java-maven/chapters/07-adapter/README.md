# 第 07 章：适配器 Adapter

## 场景

业务只认识 `PaymentGateway.pay(PaymentRequest)`，第三方 SDK 却接收“分”为单位的参数并返回状态码。对象适配器持有 SDK，完成方法、参数、单位和返回值四层翻译，客户端无需感知不兼容接口。

```powershell
.\scripts\run.ps1 07
```

断言验证 `19.99` 元被精确转成 `1999` 分，并将 `code=0` 转成统一成功结果。对象适配器优于类适配器之处在于可替换 Adaptee、减少继承约束。生产中金额应使用 `BigDecimal`/最小货币单位，示例正是如此。

