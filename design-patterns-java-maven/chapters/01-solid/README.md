# 第 01 章：设计模式概论与 SOLID 原则

## 场景

通知系统需要支持邮件和短信。示例把变化点拆成小接口，并通过构造器注入实现，集中展示 SOLID：

- **SRP**：`NotificationService` 只编排发送，模板渲染和通道发送各自负责；
- **OCP**：增加 `SmsChannel` 不改业务服务；
- **LSP**：任意 `Channel` 实现都能被替换并满足“返回回执”的约定；
- **ISP**：客户端只依赖单方法小接口；
- **DIP**：高层服务依赖 `Channel`、`TemplateRenderer` 抽象。

## 运行

```powershell
.\scripts\run.ps1 01
```

预期能看到两种通道发送相同的业务消息，并以 `[PASS] chapter-01` 结束。练习：增加控制台通道，不修改 `NotificationService`。

