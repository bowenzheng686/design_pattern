#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
for id in $(seq -w 1 21); do "$root/scripts/run.sh" "$id"; done
echo '[PASS] all: 21/21 chapters passed'

