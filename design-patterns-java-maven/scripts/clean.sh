#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
find "$root/chapters" -mindepth 2 -maxdepth 2 -type d -name target -exec rm -rf -- {} +
echo '[PASS] clean'
