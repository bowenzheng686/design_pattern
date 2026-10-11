#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
python_bin="${PYTHON:-}"
if [[ -z "$python_bin" && -x "$root/.venv/bin/python" ]]; then python_bin="$root/.venv/bin/python"; fi
if [[ -z "$python_bin" ]] && command -v python3 >/dev/null; then python_bin=python3; fi
if [[ -z "$python_bin" ]] && command -v python >/dev/null; then python_bin=python; fi
[[ -n "$python_bin" ]] || { echo 'Python 3.11+ not found' >&2; exit 1; }
"$python_bin" -c 'import sys; assert sys.version_info >= (3,11); print(sys.version)'
echo '[PASS] environment'

