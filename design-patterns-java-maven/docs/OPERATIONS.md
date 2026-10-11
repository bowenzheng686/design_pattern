# Maven 版操作手册

## 工程导入

在 IntelliJ IDEA、Eclipse 或 VS Code 中打开根目录 `pom.xml`。21 个章节会显示为独立 Maven module，每个 module 都有自己的 `Main` 类。

## 命令行运行

```powershell
# 自动选择 Maven；Maven 不存在时回退到 javac
.\scripts\run.ps1 03

# 强制后备编译路径，用于排除 Maven/插件问题
.\scripts\run.ps1 03 -JdkOnly

# 一次运行全部模块
.\scripts\run-all.ps1
```

脚本会读取模块的 `module.properties`，构建到 `target/classes`，运行主类并检查 `[PASS] chapter-XX`。退出码非零或缺少 PASS 标记都视为失败。

## Maven 生命周期

```powershell
mvn clean compile
mvn package
mvn clean
```

各案例使用 JDK 标准库，没有业务第三方依赖。聚合 `package` 负责统一编译；案例的业务正确性由运行脚本执行内置断言验证。

## 综合项目

```powershell
.\scripts\run.ps1 21
.\scripts\run.ps1 21 --fail-payment
.\scripts\run.ps1 21 --invalid-order
```

三条命令分别验证正常履约、瞬时支付故障重试恢复、非法输入在支付前被责任链拦截。

## 回滚

```powershell
.\scripts\clean.ps1
```

脚本仅删除各 module 的 `target`，不会修改源码或资料。

