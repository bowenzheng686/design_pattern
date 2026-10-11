# Java 17 设计模式可运行代码库

本代码库根据 `resources/设计模式` 的 21 个章节组织。每章都包含：

- 独立的 Java 17 源码和 `main` 入口；
- 足够解释角色、调用方向与取舍的中文注释；
- 可复制的运行命令、预期输出与练习建议；
- 内置断言，结果错误时以非零状态退出；
- `[PASS] chapter-XX` 验收标记。

## 快速开始

要求：JDK 17 或更高版本，`java` 与 `javac` 均在 `PATH` 中。不需要 Maven、Gradle、数据库或网络。

```powershell
# Windows PowerShell：先检查环境
.\scripts\doctor.ps1

# 运行单章（01～21）
.\scripts\run.ps1 03

# 编译并运行全部章节
.\scripts\run-all.ps1

# 清除全部生成物
.\scripts\clean.ps1
```

Linux/macOS：

```bash
chmod +x scripts/*.sh
./scripts/doctor.sh
./scripts/run.sh 03
./scripts/run-all.sh
./scripts/clean.sh
```

## 章节导航

| 章 | 主题 | 可运行案例 |
|---:|---|---|
| 01 | 设计模式概论与 SOLID | 通知服务的五原则验证 |
| 02 | 单例 | 枚举配置中心与并发唯一性 |
| 03 | 工厂方法 | 多目标日志工厂 |
| 04 | 抽象工厂 | Windows/macOS UI 产品族 |
| 05 | 建造者 | 游戏电脑与办公电脑装配 |
| 06 | 原型 | 文档模板注册表与深拷贝 |
| 07 | 适配器 | 第三方支付接口转换 |
| 08 | 桥接 | 形状与渲染器两个变化维度 |
| 09 | 装饰器 | 咖啡配料的动态组合 |
| 10 | 代理 | 图片延迟加载与缓存 |
| 11 | 外观 | 家庭影院一键控制 |
| 12 | 观察者 | 气象站 Pull 模型 |
| 13 | 策略 | 可替换的电商运费算法 |
| 14 | 模板方法 | CSV/JSON 数据导入骨架 |
| 15 | 命令 | 文本编辑器撤销/重做/宏命令 |
| 16 | 责任链 | 动态请假审批链 |
| 17 | 状态 | 自动售货机状态转换 |
| 18 | 实战速查 | 需求到模式的决策顾问 |
| 19 | 学习路线 | 四阶段学习计划生成器 |
| 20 | 环境与排错 | 可执行环境诊断与实验记录 |
| 21 | 综合实战 | 可观测订单履约系统与故障恢复 |

## 文档

- [环境配置](docs/ENVIRONMENT.md)
- [操作手册](docs/OPERATIONS.md)
- [架构与模式映射](docs/ARCHITECTURE.md)
- [排错手册](docs/TROUBLESHOOTING.md)
- [综合项目验收](chapters/21-comprehensive-order-system/README.md)

## 目录约定

每个章节都遵循相同结构：

```text
chapters/XX-topic/
├── README.md
├── chapter.properties     # main.class 与验收标记
└── src/...                # Java 源码
```

编译产物只进入当前章节的 `build/`，可以随时删除。源码不写入用户目录，不访问网络。

