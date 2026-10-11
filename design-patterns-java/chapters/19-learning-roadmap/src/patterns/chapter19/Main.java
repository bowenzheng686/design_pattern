package patterns.chapter19;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/** 第 19 章入口：可执行地验证学习路线覆盖率，而不是只输出一段静态文字。 */
public final class Main {
    record Stage(String name, List<Integer> chapters, String goal, String evidence) {}

    public static void main(String[] args) {
        List<Stage> stages = List.of(
                new Stage("A 认识", List.of(1, 2, 3, 4), "说清对象、边界和典型用途", "运行 + 口述"),
                new Stage("B 操作", List.of(5, 6, 7, 8, 9), "修改一个变量并验证输出", "代码差异 + 断言"),
                new Stage("C 原理", List.of(10, 11, 12, 13, 14), "解释依赖方向与取舍", "结构图 + 对比"),
                new Stage("D 实战", List.of(15, 16, 17, 18), "组合、排错并验收", "全量脚本 + 复盘"));

        Set<Integer> covered = new HashSet<>();
        for (Stage stage : stages) {
            System.out.printf("%s | chapters=%s | goal=%s | evidence=%s%n",
                    stage.name(), stage.chapters(), stage.goal(), stage.evidence());
            for (int chapter : stage.chapters()) {
                if (!covered.add(chapter)) throw new IllegalStateException("章节重复: " + chapter);
            }
        }

        Set<Integer> expected = new HashSet<>();
        for (int i = 1; i <= 18; i++) expected.add(i);
        if (!covered.equals(expected)) throw new IllegalStateException("学习路线未完整覆盖 01-18: " + covered);
        System.out.println("first-round-check=explain + run + observe + troubleshoot");
        System.out.println("[PASS] chapter-19");
    }
}

