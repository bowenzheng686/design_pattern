# Java 17 设计模式代码库（Maven 多模块版）

本工程是基于 `resources/设计模式` 21 章资料生成的第二份 Java 代码库。与纯 JDK 目录版不同，本版采用标准 Maven 聚合结构：

- 父工程统一控制 Java 17、UTF-8 和插件版本；
- 每章是可独立构建、运行和导入 IDE 的 Maven module；
- 源码位于 `src/main/java`，构建产物位于 `target/classes`；
- 每章带独立 README、详细中文注释、运行断言与 PASS 标记；
- 第 21 章为组合多种模式的订单履约综合实战。

## 快速开始

```powershell
# 检查 JDK/Maven。Maven 缺失时运行脚本会自动使用 javac
.\scripts\doctor.ps1

# 单章运行
.\scripts\run.ps1 09

# 运行全部 21 章
.\scripts\run-all.ps1

# 强制使用纯 JDK 后备编译器
.\scripts\run.ps1 09 -JdkOnly

# 综合项目三条验收路径
.\scripts\run.ps1 21
.\scripts\run.ps1 21 --fail-payment
.\scripts\run.ps1 21 --invalid-order

# 清理所有 target
.\scripts\clean.ps1
```

Maven 原生命令：

```powershell
# 聚合构建
mvn clean package

# 只构建一个模块
mvn -f .\chapters\13-strategy\pom.xml clean package

# 运行已构建模块
java -cp .\chapters\13-strategy\target\classes patterns.chapter13.Main
```

## 章节

| 范围 | 内容 |
|---|---|
| 01 | SOLID 原则与依赖方向 |
| 02–06 | 单例、工厂方法、抽象工厂、建造者、原型 |
| 07–11 | 适配器、桥接、装饰器、代理、外观 |
| 12–17 | 观察者、策略、模板方法、命令、责任链、状态 |
| 18–20 | 模式选择、学习路线、环境与排错 |
| 21 | 可观测订单履约综合项目 |

## 文档入口

- [环境配置](docs/ENVIRONMENT.md)
- [操作手册](docs/OPERATIONS.md)
- [工程架构](docs/ARCHITECTURE.md)
- [排错手册](docs/TROUBLESHOOTING.md)
- [综合项目](chapters/21-comprehensive-order-system/README.md)

