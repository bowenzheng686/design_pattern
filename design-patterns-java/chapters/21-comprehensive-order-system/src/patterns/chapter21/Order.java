package patterns.chapter21;

import java.util.ArrayList;
import java.util.List;

/**
 * 聚合根与 State 模式的 Context。
 * 订单的稳定数据不可变；只有生命周期状态可变，而且只能由当前 State 决定合法动作。
 */
final class Order {
    record LineItem(String sku, int quantity, long unitPriceInCents, boolean inStock) {
        long subtotal() { return Math.multiplyExact(quantity, unitPriceInCents); }
    }

    interface State {
        String name();
        void pay(Order order);
        void ship(Order order);
    }

    static final class NewState implements State {
        public String name() { return "NEW"; }
        public void pay(Order order) { order.transition(new PaidState()); }
        public void ship(Order order) { throw new IllegalStateException("NEW 订单不能发货"); }
    }

    static final class PaidState implements State {
        public String name() { return "PAID"; }
        public void pay(Order order) { throw new IllegalStateException("订单不可重复支付"); }
        public void ship(Order order) { order.transition(new ShippedState()); }
    }

    static final class ShippedState implements State {
        public String name() { return "SHIPPED"; }
        public void pay(Order order) { throw new IllegalStateException("已发货订单不可支付"); }
        public void ship(Order order) { throw new IllegalStateException("订单不可重复发货"); }
    }

    private final String id;
    private final String customer;
    private final String contact;
    private final List<LineItem> items;
    private State state = new NewState();

    private Order(Builder builder) {
        this.id = builder.id;
        this.customer = builder.customer;
        this.contact = builder.contact;
        this.items = List.copyOf(builder.items);
    }

    String id() { return id; }
    String customer() { return customer; }
    String contact() { return contact; }
    List<LineItem> items() { return items; }
    String stateName() { return state.name(); }
    long subtotal() { return items.stream().mapToLong(LineItem::subtotal).reduce(0, Math::addExact); }
    void pay() { state.pay(this); }
    void ship() { state.ship(this); }
    State snapshotState() { return state; }

    /** 只给 Command 的补偿逻辑使用；正常业务状态转换必须经过 State 行为。 */
    void restoreState(State snapshot) { this.state = snapshot; }
    private void transition(State next) { this.state = next; }

    static Builder builder(String id) { return new Builder(id); }

    /** Builder 分离可读的分步构造，并在 build 出口保护结构不变量。 */
    static final class Builder {
        private final String id;
        private String customer = "";
        private String contact = "";
        private final List<LineItem> items = new ArrayList<>();
        private Builder(String id) { this.id = id; }
        Builder customer(String value) { customer = value; return this; }
        Builder contact(String value) { contact = value; return this; }
        Builder addItem(String sku, int quantity, long unitPriceInCents, boolean inStock) {
            items.add(new LineItem(sku, quantity, unitPriceInCents, inStock));
            return this;
        }
        Order build() {
            // 客户名等业务有效性刻意留给责任链，以演示拒绝路径；这里只保证对象可安全存在。
            if (id == null || id.isBlank()) throw new IllegalStateException("订单 ID 必填");
            if (items.isEmpty()) throw new IllegalStateException("至少需要一个行项目");
            return new Order(this);
        }
    }
}

