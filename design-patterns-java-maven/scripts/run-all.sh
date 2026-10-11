#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
mode="${1:-}"
for id in $(seq -w 1 21); do
  if [[ -n "$mode" ]]; then "$root/scripts/run.sh" "$id" "$mode"
  else "$root/scripts/run.sh" "$id"
  fi
done
echo '[PASS] all: 21/21 modules passed'
