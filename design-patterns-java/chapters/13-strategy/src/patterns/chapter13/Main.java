package patterns.chapter13;

import java.util.LinkedHashMap;
import java.util.Map;

/** 第 13 章入口：把经常变化的运费算法封装为可以自由替换的对象。 */
public final class Main {
    record Shipment(int weightGrams, int distanceKm) {}
    interface ShippingStrategy { String name(); int feeInCents(Shipment shipment); }

    static final class StandardShipping implements ShippingStrategy {
        public String name() { return "standard"; }
        public int feeInCents(Shipment s) { return 500 + (s.weightGrams() / 1000) * 100; }
    }
    static final class ExpressShipping implements ShippingStrategy {
        public String name() { return "express"; }
        public int feeInCents(Shipment s) { return 1000 + s.distanceKm() * 20; }
    }
    static final class StorePickup implements ShippingStrategy {
        public String name() { return "pickup"; }
        public int feeInCents(Shipment s) { return 0; }
    }

    static final class Order {
        private final Shipment shipment;
        Order(Shipment shipment) { this.shipment = shipment; }
        /** Context 不知道算法细节，只维护一次稳定委派。 */
        int shippingFee(ShippingStrategy strategy) { return strategy.feeInCents(shipment); }
    }

    public static void main(String[] args) {
        Order order = new Order(new Shipment(2500, 30));
        Map<String, Integer> fees = new LinkedHashMap<>();
        for (ShippingStrategy strategy : new ShippingStrategy[]{
                new StandardShipping(), new ExpressShipping(), new StorePickup()}) {
            fees.put(strategy.name(), order.shippingFee(strategy));
        }
        fees.forEach((name, fee) -> System.out.println(name + "=" + fee + " cents"));
        if (!fees.equals(Map.of("standard", 700, "express", 1600, "pickup", 0)))
            throw new IllegalStateException("策略算法结果错误");
        System.out.println("[PASS] chapter-13");
    }
}

