package patterns.chapter15;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

/** 第 15 章入口：请求成为一等对象后，可以保存、排序、组合和逆向执行。 */
public final class Main {
    static final class TextEditor {
        private String text = "";
        void append(String value) { text += value; }
        void restore(String snapshot) { text = snapshot; }
        String text() { return text; }
    }

    interface Command { void execute(); void undo(); String name(); }

    static final class AppendCommand implements Command {
        private final TextEditor editor; private final String value;
        private String before;
        AppendCommand(TextEditor editor, String value) { this.editor = editor; this.value = value; }
        public void execute() { before = editor.text(); editor.append(value); }
        public void undo() { editor.restore(before); }
        public String name() { return "append(" + value + ")"; }
    }

    /** 宏命令组合多个命令；撤销必须逆序，才能正确恢复依赖顺序。 */
    static final class MacroCommand implements Command {
        private final String name; private final List<Command> commands;
        MacroCommand(String name, List<Command> commands) { this.name = name; this.commands = List.copyOf(commands); }
        public void execute() { commands.forEach(Command::execute); }
        public void undo() {
            for (int i = commands.size() - 1; i >= 0; i--) commands.get(i).undo();
        }
        public String name() { return name; }
    }

    static final class CommandHistory {
        private final Deque<Command> undo = new ArrayDeque<>();
        private final Deque<Command> redo = new ArrayDeque<>();
        private final List<String> trace = new ArrayList<>();
        void execute(Command command) {
            command.execute(); undo.push(command); redo.clear(); trace.add("execute:" + command.name());
        }
        void undo() {
            if (undo.isEmpty()) return;
            Command command = undo.pop(); command.undo(); redo.push(command); trace.add("undo:" + command.name());
        }
        void redo() {
            if (redo.isEmpty()) return;
            Command command = redo.pop(); command.execute(); undo.push(command); trace.add("redo:" + command.name());
        }
    }

    private static void expect(TextEditor editor, String expected) {
        if (!editor.text().equals(expected)) throw new IllegalStateException("expected=" + expected + ", actual=" + editor.text());
        System.out.println("text='" + editor.text() + "'");
    }

    public static void main(String[] args) {
        TextEditor editor = new TextEditor(); CommandHistory history = new CommandHistory();
        history.execute(new AppendCommand(editor, "Hello"));
        history.execute(new AppendCommand(editor, " Patterns")); expect(editor, "Hello Patterns");
        history.undo(); expect(editor, "Hello");
        history.redo(); expect(editor, "Hello Patterns");
        MacroCommand macro = new MacroCommand("punctuation", List.of(
                new AppendCommand(editor, "!"), new AppendCommand(editor, "!")));
        history.execute(macro); expect(editor, "Hello Patterns!!");
        history.undo(); expect(editor, "Hello Patterns");
        System.out.println("[PASS] chapter-15");
    }
}

