package patterns.chapter09;

/** 第 09 章入口：运行时按需叠加职责，每一层保持与原对象相同的接口。 */
public final class Main {
    interface Beverage { String description(); int price(); }

    static final class Espresso implements Beverage {
        public String description() { return "浓缩咖啡"; }
        public int price() { return 10; }
    }

    /** 基础装饰器保存委托对象，共享组件类型使装饰器可以继续被包装。 */
    abstract static class BeverageDecorator implements Beverage {
        protected final Beverage delegate;
        BeverageDecorator(Beverage delegate) { this.delegate = delegate; }
    }
    static final class Milk extends BeverageDecorator {
        Milk(Beverage delegate) { super(delegate); }
        public String description() { return delegate.description() + " + 牛奶"; }
        public int price() { return delegate.price() + 3; }
    }
    static final class Mocha extends BeverageDecorator {
        Mocha(Beverage delegate) { super(delegate); }
        public String description() { return delegate.description() + " + 摩卡"; }
        public int price() { return delegate.price() + 4; }
    }

    public static void main(String[] args) {
        // 包装顺序就是职责执行顺序；同一种装饰器也可以重复使用。
        Beverage order = new Mocha(new Mocha(new Milk(new Espresso())));
        System.out.println(order.description());
        System.out.println("price=" + order.price());
        if (order.price() != 21 || !order.description().endsWith("摩卡 + 摩卡"))
            throw new IllegalStateException("装饰链结果错误");
        System.out.println("[PASS] chapter-09");
    }
}

