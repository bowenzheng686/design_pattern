# Maven 版排错手册

| 现象 | 检查 | 处理 |
|---|---|---|
| `mvn` 找不到 | `Get-Command mvn` | 安装 Maven 3.9+，或使用 `-JdkOnly` |
| Maven 使用错误 JDK | `mvn -version` | 修正 `JAVA_HOME` 后重开终端 |
| 插件下载失败 | 查看 Maven 首个网络异常 | 检查代理/镜像；已有缓存时用 `mvn -o` |
| 编码异常 | 检查终端和 `project.build.sourceEncoding` | 保持 UTF-8；优先用仓库脚本 |
| 找不到主类 | 检查 `module.properties` 和 `target/classes` | 重跑该章脚本，不手拼 classpath |
| 内置断言异常 | 读取异常前的业务输出 | 对照章节 README 的角色和预期结果 |

排错顺序：`doctor` → 单模块 `-JdkOnly` → 单模块 Maven 构建 → 聚合构建。这样可以区分 JDK、源码、Maven 插件和聚合配置四个层次。

