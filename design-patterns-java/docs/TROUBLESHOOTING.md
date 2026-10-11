# 排错手册

按“环境 → 输入 → 编译 → 运行 → 断言”从外向内排查，每次只改一个变量。

| 现象 | 常见原因 | 观测命令 | 处理 |
|---|---|---|---|
| 找不到 `javac` | 安装的是 JRE 或 PATH 未刷新 | `Get-Command javac` | 安装 JDK 17 并重开终端 |
| `invalid target release` | 编译器低于 17 | `javac -version` | 切换 `JAVA_HOME` 与 PATH |
| 中文乱码 | 终端编码不一致 | `[Console]::OutputEncoding` | 脚本已设置 UTF-8；用新终端重试 |
| 找不到主类 | 手工 classpath/包名错误 | 查看 `chapter.properties` | 优先使用 `scripts/run.*` |
| `[FAIL]` 或异常 | 内置业务断言不成立 | 阅读异常前最后一行 | 对照该章 README 的预期输出 |
| PowerShell 禁止脚本 | 执行策略限制 | `Get-ExecutionPolicy` | 使用 `powershell -ExecutionPolicy Bypass -File ...` |

## 最小复现记录

```text
章节：
操作系统与 JDK：
执行命令：
预期结果：
实际结果：
首次异常堆栈：
最近变化：
已验证假设：
清理/回滚结果：
```

优先保留文本日志，不只保留截图。修复后必须重放原命令，并再运行一次 `run-all` 防止回归。

