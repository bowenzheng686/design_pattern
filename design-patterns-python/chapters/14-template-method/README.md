# 第 14 章：模板方法 Template Method

父类 `import_data()` 固定读取、解析、钩子、校验、保存和收尾顺序；CSV/JSON Lines 子类只实现解析变化点。运行：`python tools/run_chapter.py 14`。两种导入器的 trace 必须相同。
