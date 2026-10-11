package patterns.chapter03;

import java.util.ArrayList;
import java.util.List;

/** 第 03 章入口：父类固定使用产品的流程，子类只负责决定具体产品。 */
public final class Main {
    interface Logger { String write(String level, String message); }

    static final class ConsoleLogger implements Logger {
        public String write(String level, String message) { return "CONSOLE [" + level + "] " + message; }
    }

    static final class MemoryLogger implements Logger {
        private final List<String> entries;
        MemoryLogger(List<String> entries) { this.entries = entries; }
        public String write(String level, String message) {
            String line = "MEMORY [" + level + "] " + message;
            entries.add(line);
            return line;
        }
    }

    /** Creator：record() 是稳定流程；createLogger() 把实例化决策延迟给子类。 */
    abstract static class LoggerFactory {
        protected abstract Logger createLogger();
        final String record(String level, String message) {
            // 客户端流程只认识 Logger 接口，因此新增产品不需要修改此处。
            return createLogger().write(level.toUpperCase(), message.strip());
        }
    }

    static final class ConsoleLoggerFactory extends LoggerFactory {
        protected Logger createLogger() { return new ConsoleLogger(); }
    }

    static final class MemoryLoggerFactory extends LoggerFactory {
        private final List<String> entries;
        MemoryLoggerFactory(List<String> entries) { this.entries = entries; }
        protected Logger createLogger() { return new MemoryLogger(entries); }
    }

    private static void require(boolean condition, String message) {
        if (!condition) throw new IllegalStateException(message);
    }

    public static void main(String[] args) {
        List<String> entries = new ArrayList<>();
        List<LoggerFactory> factories = List.of(new ConsoleLoggerFactory(), new MemoryLoggerFactory(entries));
        List<String> results = factories.stream().map(f -> f.record("info", "order created")).toList();
        results.forEach(System.out::println);
        require(results.get(0).startsWith("CONSOLE"), "控制台工厂应创建控制台产品");
        require(entries.size() == 1 && entries.get(0).startsWith("MEMORY"), "内存工厂应创建并写入内存产品");
        System.out.println("[PASS] chapter-03");
    }
}

