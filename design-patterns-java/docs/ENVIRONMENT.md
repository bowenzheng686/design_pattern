# 本地环境配置

## 1. 必需环境

| 项目 | 要求 | 检查命令 |
|---|---|---|
| JDK | 17+（必须是 JDK，不只是 JRE） | `java -version`、`javac -version` |
| PowerShell | Windows 5.1+ 或 PowerShell 7+ | `$PSVersionTable.PSVersion` |
| Bash | Linux/macOS 的常规 Bash | `bash --version` |
| 编码 | UTF-8 | 编译脚本已固定 `-encoding UTF-8` |

没有任何第三方依赖，也不要求 Maven/Gradle。选择 JDK 17 是为了使用现代 Java，同时保留长期支持版本的可复现性。

## 2. 安装建议

安装任一 JDK 17 发行版后设置：

- `JAVA_HOME` 指向 JDK 根目录；
- 将 `%JAVA_HOME%\bin`（Windows）或 `$JAVA_HOME/bin`（Linux/macOS）加入 `PATH`；
- 重新打开终端，确保 `java` 与 `javac` 来自同一个 JDK。

Windows 验证：

```powershell
Get-Command java
Get-Command javac
java -version
javac -version
.\scripts\doctor.ps1
```

Linux/macOS 验证：

```bash
command -v java
command -v javac
java -version
javac -version
./scripts/doctor.sh
```

## 3. 成功标准

诊断脚本最后输出 `[PASS] environment`；随后运行第 01 章，最后应看到 `[PASS] chapter-01`。

## 4. 环境边界

- 示例只使用 JDK 标准库；
- 示例不启动常驻服务、不占端口、不访问网络；
- `build/` 是唯一构建目录，`clean` 可完整回滚；
- 所有业务输入都在内存中构造，保证重复执行结果一致。

