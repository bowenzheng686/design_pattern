# Maven 版环境配置

## 必需环境

| 软件 | 版本 | 验证命令 |
|---|---:|---|
| JDK | 17+ | `java -version`、`javac -version` |
| Maven | 3.9+（推荐） | `mvn -version` |
| PowerShell 或 Bash | 常规版本 | `$PSVersionTable` / `bash --version` |

Maven 是标准构建入口；运行脚本同时提供 `javac` 后备路径，所以只安装 JDK 17 也能运行全部案例。

## Windows 配置

1. 安装 JDK 17；
2. 设置 `JAVA_HOME` 为 JDK 根目录，并把 `%JAVA_HOME%\bin` 加入 `PATH`；
3. 安装 Maven 3.9+，设置 `MAVEN_HOME`，把 `%MAVEN_HOME%\bin` 加入 `PATH`；
4. 重新打开终端并运行：

```powershell
java -version
javac -version
mvn -version
.\scripts\doctor.ps1
```

首次 Maven 构建可能从中央仓库获取标准插件。后续可使用本地仓库离线构建：`mvn -o package`。

## 成功标准

- JDK 检查输出版本 17 或更高；
- Maven 和 Java 使用同一 `JAVA_HOME`；
- `scripts/run-all.ps1` 最后输出 `21/21 modules passed`。

