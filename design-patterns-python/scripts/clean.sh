#!/usr/bin/env bash
set -euo pipefail
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
find "$root" -type d \( -name __pycache__ -o -name .pytest_cache -o -name .ruff_cache -o -name .mypy_cache -o -name build -o -name dist -o -name '*.egg-info' \) -prune -exec rm -rf -- {} +
echo '[PASS] clean'
