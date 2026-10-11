#!/usr/bin/env bash
set -euo pipefail
command -v java >/dev/null; command -v javac >/dev/null
major="$(javac -version 2>&1 | sed -E 's/.* ([0-9]+).*/\1/')"
[[ "$major" -ge 17 ]] || { echo 'JDK 17+ required' >&2; exit 1; }
java -version 2>&1 | head -n 1; javac -version
if command -v mvn >/dev/null; then mvn -version | head -n 2; else echo 'Maven not found; JDK fallback enabled.'; fi
echo '[PASS] environment'

