package patterns.chapter14;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/** 第 14 章入口：父类控制完整算法顺序，并在少数步骤把控制权交给子类。 */
public final class Main {
    abstract static class DataImporter {
        private final List<String> trace = new ArrayList<>();

        /** final 防止子类打乱资源读取、校验、保存的必要顺序。 */
        public final List<String> importData(String source) {
            trace.add("read");
            String raw = read(source);
            trace.add("parse");
            List<String> records = new ArrayList<>(parse(raw));
            if (discardFirstRecord() && !records.isEmpty()) records.remove(0); // 可选 Hook。
            trace.add("validate");
            records.forEach(this::validate);
            trace.add("save");
            List<String> saved = save(records);
            afterImport(saved); // 默认空实现的 Hook，子类按需参与。
            return saved;
        }
        protected String read(String source) { return source.strip(); }
        protected abstract List<String> parse(String raw);
        protected boolean discardFirstRecord() { return false; }
        protected void validate(String record) {
            if (record.isBlank()) throw new IllegalArgumentException("空记录");
        }
        protected List<String> save(List<String> records) { return List.copyOf(records); }
        protected void afterImport(List<String> records) { trace.add("finish:" + records.size()); }
        List<String> trace() { return List.copyOf(trace); }
    }

    static final class CsvImporter extends DataImporter {
        protected List<String> parse(String raw) { return Arrays.stream(raw.split("\\R")).toList(); }
        protected boolean discardFirstRecord() { return true; }
    }
    static final class JsonLinesImporter extends DataImporter {
        protected List<String> parse(String raw) { return Arrays.stream(raw.split("\\R")).toList(); }
    }

    public static void main(String[] args) {
        DataImporter csv = new CsvImporter();
        DataImporter json = new JsonLinesImporter();
        List<String> csvRecords = csv.importData("name\nAda\nLin");
        List<String> jsonRecords = json.importData("{\"name\":\"Ada\"}\n{\"name\":\"Lin\"}");
        System.out.println("csv=" + csvRecords + " trace=" + csv.trace());
        System.out.println("json=" + jsonRecords + " trace=" + json.trace());
        if (csvRecords.size() != 2 || jsonRecords.size() != 2 || !csv.trace().equals(json.trace()))
            throw new IllegalStateException("模板骨架或钩子错误");
        System.out.println("[PASS] chapter-14");
    }
}

