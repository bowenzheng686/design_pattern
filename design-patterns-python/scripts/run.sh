#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
python_bin="${PYTHON:-}"
if [[ -z "$python_bin" && -x "$root/.venv/bin/python" ]]; then python_bin="$root/.venv/bin/python"; fi
if [[ -z "$python_bin" ]] && command -v python3 >/dev/null; then python_bin=python3; fi
if [[ -z "$python_bin" ]] && command -v python >/dev/null; then python_bin=python; fi
chapter="${1:?usage: ./scripts/run.sh CHAPTER [args...]}"; shift
"$python_bin" "$root/tools/run_chapter.py" "$chapter" "$@"

