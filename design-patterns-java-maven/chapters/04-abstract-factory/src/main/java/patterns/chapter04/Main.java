package patterns.chapter04;

import java.util.List;

/** 第 04 章入口：创建相互匹配的一整族对象，而非只创建一个对象。 */
public final class Main {
    interface Button { String theme(); String paint(); }
    interface Checkbox { String theme(); String paint(); }

    /** AbstractFactory 的每个方法代表产品族中的一个产品等级结构。 */
    interface UiFactory {
        Button createButton();
        Checkbox createCheckbox();
    }

    record WindowsButton() implements Button {
        public String theme() { return "Windows"; }
        public String paint() { return "Windows square button"; }
    }
    record WindowsCheckbox() implements Checkbox {
        public String theme() { return "Windows"; }
        public String paint() { return "Windows tick checkbox"; }
    }
    record MacButton() implements Button {
        public String theme() { return "macOS"; }
        public String paint() { return "macOS rounded button"; }
    }
    record MacCheckbox() implements Checkbox {
        public String theme() { return "macOS"; }
        public String paint() { return "macOS toggle checkbox"; }
    }

    static final class WindowsFactory implements UiFactory {
        public Button createButton() { return new WindowsButton(); }
        public Checkbox createCheckbox() { return new WindowsCheckbox(); }
    }
    static final class MacFactory implements UiFactory {
        public Button createButton() { return new MacButton(); }
        public Checkbox createCheckbox() { return new MacCheckbox(); }
    }

    /** Client 不判断平台；选择工厂发生在组合根，业务内部始终操作抽象产品。 */
    record Screen(String theme, String description) {}
    static Screen buildSettingsScreen(UiFactory factory) {
        Button button = factory.createButton();
        Checkbox checkbox = factory.createCheckbox();
        if (!button.theme().equals(checkbox.theme())) throw new IllegalStateException("产品族混用");
        return new Screen(button.theme(), button.paint() + " + " + checkbox.paint());
    }

    public static void main(String[] args) {
        List<Screen> screens = List.of(buildSettingsScreen(new WindowsFactory()), buildSettingsScreen(new MacFactory()));
        screens.forEach(s -> System.out.println(s.theme() + " => " + s.description()));
        if (!screens.get(0).theme().equals("Windows") || !screens.get(1).theme().equals("macOS"))
            throw new IllegalStateException("工厂必须生产正确家族");
        System.out.println("[PASS] chapter-04");
    }
}

