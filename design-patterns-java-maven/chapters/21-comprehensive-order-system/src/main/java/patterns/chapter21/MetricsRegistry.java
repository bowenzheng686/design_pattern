package patterns.chapter21;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 枚举单例作为进程内演示指标入口。生产中通常把 Metrics 接口注入，以获得测试隔离和后端可替换性。
 */
enum MetricsRegistry {
    INSTANCE;
    private final Map<String, Long> counters = new LinkedHashMap<>();
    synchronized void increment(String name) { counters.merge(name, 1L, Long::sum); }
    synchronized long value(String name) { return counters.getOrDefault(name, 0L); }
    synchronized Map<String, Long> snapshot() { return Map.copyOf(counters); }
    synchronized void reset() { counters.clear(); }
}

