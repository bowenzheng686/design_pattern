package patterns.chapter21;

/** Strategy：同一个订单上下文可使用不同定价算法，工作流不出现会员类型分支。 */
interface PricingStrategy {
    String name();
    long payableInCents(Order order);
}

final class RegularPricingStrategy implements PricingStrategy {
    public String name() { return "regular"; }
    public long payableInCents(Order order) { return order.subtotal(); }
}

final class VipPricingStrategy implements PricingStrategy {
    public String name() { return "vip-10%-off"; }
    public long payableInCents(Order order) {
        // 全程整数运算；先乘后除保留确定的向下取整语义。
        return Math.multiplyExact(order.subtotal(), 90) / 100;
    }
}

