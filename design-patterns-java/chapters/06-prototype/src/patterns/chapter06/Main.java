package patterns.chapter06;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** 第 06 章入口：通过注册表克隆昂贵模板，并验证可变子对象完全隔离。 */
public final class Main {
    interface Prototype<T> { T copy(); }

    static final class Document implements Prototype<Document> {
        private String title;
        private final List<String> tags;

        Document(String title, List<String> tags) {
            this.title = title;
            // 构造入口也防御性复制，避免调用者保留引用后从外部修改原型。
            this.tags = new ArrayList<>(tags);
        }

        /** 显式复制让深浅边界一目了然：String 可共享，List 必须新建。 */
        private Document(Document source) {
            this.title = source.title;
            this.tags = new ArrayList<>(source.tags);
        }
        public Document copy() { return new Document(this); }
        void title(String value) { title = value; }
        void addTag(String value) { tags.add(value); }
        String title() { return title; }
        List<String> tags() { return List.copyOf(tags); }
        public String toString() { return title + tags; }
    }

    static final class PrototypeRegistry {
        private final Map<String, Document> prototypes = new HashMap<>();
        void register(String key, Document prototype) { prototypes.put(key, prototype.copy()); }
        Document create(String key) {
            Document prototype = prototypes.get(key);
            if (prototype == null) throw new IllegalArgumentException("未知原型: " + key);
            return prototype.copy();
        }
    }

    public static void main(String[] args) {
        PrototypeRegistry registry = new PrototypeRegistry();
        registry.register("report", new Document("标准报告", List.of("internal", "draft")));
        Document first = registry.create("report");
        Document second = registry.create("report");
        first.title("第一季度报告");
        first.addTag("finance");

        System.out.println("first=" + first);
        System.out.println("second=" + second);
        Document third = registry.create("report");
        System.out.println("prototype-copy=" + third);
        if (second.tags().contains("finance") || third.tags().contains("finance") || first == second)
            throw new IllegalStateException("深拷贝隔离失败");
        System.out.println("[PASS] chapter-06");
    }
}
