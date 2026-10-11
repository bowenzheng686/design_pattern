#!/usr/bin/env bash
set -euo pipefail
command -v java >/dev/null
command -v javac >/dev/null
major="$(javac -version 2>&1 | sed -E 's/.* ([0-9]+).*/\1/')"
[[ "$major" -ge 17 ]] || { echo "JDK 17+ required" >&2; exit 1; }
uname -a
java -version 2>&1 | head -n 1
javac -version
echo '[PASS] environment'

