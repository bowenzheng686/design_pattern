# 第 03 章：工厂方法 Factory Method

## 场景

日志系统支持控制台和内存输出。`LoggerFactory` 的 `createLogger()` 是工厂方法，子类决定创建哪个产品；父类的 `record()` 保留稳定业务流程。客户端依赖抽象工厂，不直接 `new` 具体日志器。

```powershell
.\scripts\run.ps1 03
```

输出会证明两个工厂创建不同产品但遵守相同契约。增加远程日志时新增 `RemoteLogger` 与 `RemoteLoggerFactory`，无需改现有工厂。若所有创建规则只是一段稳定映射，简单工厂可能更合适；工厂方法的代价是类数量增加。

