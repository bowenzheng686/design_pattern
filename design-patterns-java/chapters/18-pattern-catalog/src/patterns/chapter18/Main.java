package patterns.chapter18;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 第 18 章入口：把速查表变成可查询的最小决策顾问。 */
public final class Main {
    record Advice(String pattern, String intent, String caution) {}

    static final class PatternAdvisor {
        private final Map<String, Advice> catalog = new LinkedHashMap<>();
        PatternAdvisor() {
            catalog.put("unique", new Advice("Singleton", "进程内唯一实例", "需要测试隔离/多实例时用 DI 生命周期"));
            catalog.put("complex-create", new Advice("Builder", "分步构造复杂对象", "字段很少时普通构造器更简单"));
            catalog.put("incompatible", new Advice("Adapter", "翻译不兼容接口", "若只想简化调用考虑 Facade"));
            catalog.put("dynamic-feature", new Advice("Decorator", "运行时叠加职责", "层数过深会增加调试成本"));
            catalog.put("algorithm", new Advice("Strategy", "替换算法族", "只有一个算法时不要预先抽象"));
            catalog.put("undo", new Advice("Command", "封装操作并保留逆操作", "大状态应保存差量"));
            catalog.put("notify", new Advice("Observer", "一对多状态通知", "明确同步、异常和退订语义"));
            catalog.put("simplify", new Advice("Facade", "统一复杂子系统入口", "避免成为上帝类"));
            catalog.put("pipeline", new Advice("Chain of Responsibility", "请求沿可配置链处理", "必须观测未处理请求"));
            catalog.put("internal-state", new Advice("State", "行为由内部状态驱动", "少量稳定状态可用表驱动"));
        }
        Advice advise(String problem) {
            Advice advice = catalog.get(problem);
            if (advice == null) throw new IllegalArgumentException("未知关键词: " + problem + "；可选 " + catalog.keySet());
            return advice;
        }
    }

    public static void main(String[] args) {
        PatternAdvisor advisor = new PatternAdvisor();
        List<String> queries = args.length == 0
                ? List.of("complex-create", "incompatible", "algorithm", "undo", "internal-state")
                : List.of(args[0]);
        for (String query : queries) {
            Advice a = advisor.advise(query);
            System.out.printf("%-15s -> %-24s | %s | 注意: %s%n", query, a.pattern(), a.intent(), a.caution());
        }
        if (!advisor.advise("undo").pattern().equals("Command")
                || !advisor.advise("internal-state").pattern().equals("State"))
            throw new IllegalStateException("速查映射错误");
        System.out.println("[PASS] chapter-18");
    }
}

