"""操作成为对象后，可以保存历史、重做、组合和逆向执行。"""

from __future__ import annotations
from dataclasses import dataclass
from typing import Protocol


@dataclass
class TextEditor:
    text: str = ""


class Command(Protocol):
    name: str
    def execute(self) -> None: ...
    def undo(self) -> None: ...


class AppendCommand:
    def __init__(self, editor: TextEditor, value: str) -> None:
        self.editor, self.value = editor, value
        self.before = ""
        self.name = f"append({value})"

    def execute(self) -> None:
        self.before = self.editor.text
        self.editor.text += self.value

    def undo(self) -> None:
        self.editor.text = self.before


class MacroCommand:
    def __init__(self, name: str, commands: list[Command]) -> None:
        self.name, self.commands = name, tuple(commands)

    def execute(self) -> None:
        for command in self.commands: command.execute()

    def undo(self) -> None:
        for command in reversed(self.commands): command.undo()


class CommandHistory:
    def __init__(self) -> None:
        self._undo: list[Command] = []
        self._redo: list[Command] = []

    def execute(self, command: Command) -> None:
        command.execute(); self._undo.append(command); self._redo.clear()

    def undo(self) -> None:
        if self._undo:
            command = self._undo.pop(); command.undo(); self._redo.append(command)

    def redo(self) -> None:
        if self._redo:
            command = self._redo.pop(); command.execute(); self._undo.append(command)


def expect(editor: TextEditor, expected: str) -> None:
    print(f"text={editor.text!r}")
    assert editor.text == expected


def main() -> None:
    editor, history = TextEditor(), CommandHistory()
    history.execute(AppendCommand(editor, "Hello")); history.execute(AppendCommand(editor, " Patterns"))
    expect(editor, "Hello Patterns"); history.undo(); expect(editor, "Hello")
    history.redo(); expect(editor, "Hello Patterns")
    history.execute(MacroCommand("punctuation", [AppendCommand(editor, "!"), AppendCommand(editor, "!")]))
    expect(editor, "Hello Patterns!!"); history.undo(); expect(editor, "Hello Patterns")
    print("[PASS] chapter-15")


if __name__ == "__main__": main()

