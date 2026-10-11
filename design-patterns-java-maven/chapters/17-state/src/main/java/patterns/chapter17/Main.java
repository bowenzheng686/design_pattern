package patterns.chapter17;

import java.util.ArrayList;
import java.util.List;

/** 第 17 章入口：对象行为随内部状态对象变化，客户端不负责挑选状态。 */
public final class Main {
    interface VendingState {
        void insertCoin(VendingMachine machine);
        void ejectCoin(VendingMachine machine);
        void buy(VendingMachine machine);
        String name();
    }

    static final class NoCoinState implements VendingState {
        public void insertCoin(VendingMachine m) { m.log("coin accepted"); m.transition(m.hasCoin); }
        public void ejectCoin(VendingMachine m) { m.log("no coin to eject"); }
        public void buy(VendingMachine m) { m.log("insert coin first"); }
        public String name() { return "NO_COIN"; }
    }
    static final class HasCoinState implements VendingState {
        public void insertCoin(VendingMachine m) { m.log("coin already inserted"); }
        public void ejectCoin(VendingMachine m) { m.log("coin returned"); m.transition(m.noCoin); }
        public void buy(VendingMachine m) {
            if (m.inventory <= 0) { m.log("sold out; coin returned"); m.transition(m.noCoin); return; }
            m.inventory--; m.log("product dispensed"); m.transition(m.noCoin);
        }
        public String name() { return "HAS_COIN"; }
    }

    static final class VendingMachine {
        private final VendingState noCoin = new NoCoinState();
        private final VendingState hasCoin = new HasCoinState();
        private VendingState state = noCoin;
        private int inventory;
        private final List<String> trace = new ArrayList<>();
        VendingMachine(int inventory) { this.inventory = inventory; }
        void insertCoin() { state.insertCoin(this); }
        void ejectCoin() { state.ejectCoin(this); }
        void buy() { state.buy(this); }
        void transition(VendingState next) { trace.add(state.name() + "->" + next.name()); state = next; }
        void log(String value) { trace.add(value); System.out.println(value); }
        String stateName() { return state.name(); }
        int inventory() { return inventory; }
    }

    public static void main(String[] args) {
        VendingMachine machine = new VendingMachine(2);
        machine.buy();                    // NO_COIN 对购买给出拒绝，不改变库存。
        machine.insertCoin();
        machine.insertCoin();             // HAS_COIN 的响应与 NO_COIN 不同。
        machine.buy();                    // 出货后由状态对象驱动返回 NO_COIN。
        if (machine.inventory() != 1 || !machine.stateName().equals("NO_COIN"))
            throw new IllegalStateException("状态转换或库存错误");
        System.out.println("inventory=" + machine.inventory() + ", state=" + machine.stateName());
        System.out.println("[PASS] chapter-17");
    }
}
