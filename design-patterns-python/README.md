# Python 3.11+ 设计模式可运行代码库

本代码库根据 `resources/设计模式` 的 21 个章节生成，是独立的 Python 版本。示例不是 Java 语法的机械翻译，而是使用 Python 常见工具表达相同设计意图：

- `abc.ABC` / `Protocol` 描述角色契约；
- `dataclass`、不可变值对象和类型标注明确数据边界；
- 一等函数展示 Pythonic 策略；
- `copy.deepcopy` 与显式 `clone()` 解释原型边界；
- 上下文管理、回调列表和动态委派体现语言特性。

全部案例只依赖 Python 标准库，不访问网络、不启动服务、不写业务数据。

## 快速开始

要求 Python 3.11 或更高版本。

```powershell
# Windows PowerShell
.\scripts\doctor.ps1
.\scripts\run.ps1 03
.\scripts\run-all.ps1

# 综合项目三条路径
.\scripts\run.ps1 21
.\scripts\run.ps1 21 --fail-payment
.\scripts\run.ps1 21 --invalid-order

# 删除缓存并验证回滚
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

也可以直接使用当前 Python：

```bash
python tools/run_chapter.py 03
python tools/run_all.py
```

## 章节导航

| 章 | 主题 | Python 案例 |
|---:|---|---|
| 01 | SOLID | Protocol + 构造注入的通知服务 |
| 02 | 单例 | 元类单例与线程并发验证 |
| 03 | 工厂方法 | 多目标日志 Creator |
| 04 | 抽象工厂 | 跨平台 UI 产品族 |
| 05 | 建造者 | 流式电脑配置与 Director |
| 06 | 原型 | 深拷贝文档注册表 |
| 07 | 适配器 | 第三方支付协议翻译 |
| 08 | 桥接 | 形状与渲染方式独立变化 |
| 09 | 装饰器 | GoF 对象装饰咖啡组合 |
| 10 | 代理 | 图片延迟加载与缓存 |
| 11 | 外观 | 家庭影院用例入口 |
| 12 | 观察者 | 气象站 Pull 模型 |
| 13 | 策略 | 一等函数运费算法 |
| 14 | 模板方法 | CSV/JSON Lines 导入骨架 |
| 15 | 命令 | 文本撤销、重做与宏命令 |
| 16 | 责任链 | 动态请假审批链 |
| 17 | 状态 | 自动售货机状态对象 |
| 18 | 实战速查 | 可查询模式顾问 |
| 19 | 学习路线 | 四阶段覆盖校验器 |
| 20 | 环境排错 | 故障注入与回滚记录 |
| 21 | 综合实战 | 可观测订单履约系统 |

## 文档

- [本地环境配置](docs/ENVIRONMENT.md)
- [操作手册](docs/OPERATIONS.md)
- [架构与目录](docs/ARCHITECTURE.md)
- [排错手册](docs/TROUBLESHOOTING.md)
- [综合项目说明](chapters/21-comprehensive-order-system/README.md)

每章最后输出 `[PASS] chapter-XX`。统一 runner 同时验证进程退出码与 PASS 标记，避免把“没有抛异常”误当成业务正确。

