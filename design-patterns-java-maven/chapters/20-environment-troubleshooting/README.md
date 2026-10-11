# 第 20 章：实验环境与系统排错

## 可执行诊断实验

本章把“环境版本、输入、步骤、预期、实际、失败假设、结论与回滚”转成结构化 `ExperimentRecord`。程序检查 Java/JVM/编码/可用内存，注入一条错误配置并按漏斗定位，最后回滚到正常配置。

```powershell
.\scripts\run.ps1 20
```

预期输出包括诊断项、一次 `failure-injected`、一次 `recovered` 与 PASS。它不改系统环境；错误配置只存在于局部 Map，因此回滚可重复。外层环境问题先运行根目录 `scripts/doctor.ps1`。

