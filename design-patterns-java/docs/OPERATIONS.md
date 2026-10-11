# 操作手册

以下命令均在 `design-patterns-java` 根目录执行。

## 单章运行

```powershell
.\scripts\run.ps1 08
```

脚本执行四步：读取 `chapter.properties`、清理该章旧构建、用 UTF-8 编译全部 `.java`、运行主类并校验退出码。重复执行不会改变源码或累积业务数据。

也可手工运行，以第 08 章为例：

```powershell
$chapter = '.\chapters\08-bridge'
$sources = Get-ChildItem "$chapter\src" -Recurse -Filter *.java | ForEach-Object FullName
javac -encoding UTF-8 -d "$chapter\build" $sources
java -cp "$chapter\build" patterns.chapter08.Main
```

## 全量验收

```powershell
.\scripts\run-all.ps1
```

成功时总计显示 `21/21 chapters passed`。任一章编译失败、运行异常或缺少验收标记，脚本立即以非零状态退出并保留该章输出。

## 综合项目

```powershell
# 正常链路
.\scripts\run.ps1 21

# 支付第一次失败，代理重试后恢复
.\scripts\run.ps1 21 --fail-payment

# 非法订单，由责任链拦截（该业务拒绝属于预期结果）
.\scripts\run.ps1 21 --invalid-order
```

## 清理与回滚

```powershell
.\scripts\clean.ps1
```

它只删除 `chapters/*/build`。确认命令执行后不存在 `build` 目录，即回到初始源码状态。

## 推荐学习操作

1. 先运行章节并记录输出；
2. 画出角色与调用箭头；
3. 修改一个具体实现并重跑；
4. 故意破坏一个断言，观察失败位置；
5. 恢复文件并运行全量验收。

