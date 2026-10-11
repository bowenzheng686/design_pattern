package patterns.chapter20;

import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 第 20 章入口：用结构化证据演示“确认现象→单变量实验→修复→重放”。 */
public final class Main {
    record Check(String name, boolean ok, String actual) {}
    record ExperimentRecord(String goal, String input, String expected, String actual,
                            List<String> hypotheses, String conclusion) {}

    static Check validateMode(Map<String, String> config) {
        String actual = config.getOrDefault("mode", "<missing>");
        return new Check("configuration.mode", actual.equals("demo"), actual);
    }

    public static void main(String[] args) {
        int javaFeature = Runtime.version().feature();
        List<Check> environment = List.of(
                new Check("java.feature", javaFeature >= 17, Integer.toString(javaFeature)),
                new Check("default.charset", Charset.defaultCharset().name().equalsIgnoreCase("UTF-8"), Charset.defaultCharset().name()),
                new Check("max.memory", Runtime.getRuntime().maxMemory() > 32L * 1024 * 1024,
                        Runtime.getRuntime().maxMemory() / 1024 / 1024 + " MiB"));
        environment.forEach(c -> System.out.println(c.name() + "=" + c.actual() + " ok=" + c.ok()));
        if (environment.stream().anyMatch(c -> !c.ok())) throw new IllegalStateException("环境诊断失败");

        Map<String, String> config = new LinkedHashMap<>();
        config.put("mode", "prodution"); // 故障注入：只改变一个变量，模拟拼写错误。
        Check failed = validateMode(config);
        ExperimentRecord record = new ExperimentRecord(
                "证明配置错误可被检测并回滚", config.toString(), "mode=demo", "mode=" + failed.actual(),
                List.of("路径错误", "配置未加载", "值拼写错误"), "值拼写错误");
        System.out.println("failure-injected=" + record);
        if (failed.ok()) throw new IllegalStateException("故障注入未被检测");

        config.put("mode", "demo"); // 回滚/修复后重放同一个校验，而不是换测试条件。
        Check recovered = validateMode(config);
        System.out.println("recovered=" + recovered);
        if (!recovered.ok()) throw new IllegalStateException("回滚失败");
        System.out.println("[PASS] chapter-20");
    }
}
