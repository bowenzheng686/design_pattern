# 第 04 章：抽象工厂 Abstract Factory

`UiFactory` 创建按钮和复选框这一整族产品；Windows/macOS 工厂保证族内主题一致。运行：`python tools/run_chapter.py 04`。新增产品族符合开闭原则；新增产品类型则需要修改所有工厂。
