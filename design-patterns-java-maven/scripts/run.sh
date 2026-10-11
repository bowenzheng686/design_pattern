#!/usr/bin/env bash
set -euo pipefail
chapter="${1:?usage: ./scripts/run.sh CHAPTER [--jdk-only] [app args...]}"; shift
printf -v id '%02d' "$((10#$chapter))"
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
module="$(find "$root/chapters" -maxdepth 1 -type d -name "$id-*" -print -quit)"
[[ -n "$module" ]] || { echo "Unknown chapter: $id" >&2; exit 1; }
main_class="$(sed -n 's/^main.class=//p' "$module/module.properties")"
pass_marker="$(sed -n 's/^pass.marker=//p' "$module/module.properties")"
jdk_only=false
if [[ "${1:-}" == '--jdk-only' ]]; then jdk_only=true; shift; fi
classes="$module/target/classes"
if command -v mvn >/dev/null && [[ "$jdk_only" == false ]]; then
  echo "==> [$id] Maven compile $(basename "$module")"
  mvn -q -f "$module/pom.xml" package -DskipTests
else
  echo "==> [$id] JDK fallback compile $(basename "$module")"
  rm -rf "$classes"; mkdir -p "$classes"
  find "$module/src/main/java" -name '*.java' -print > "$module/target/sources.txt"
  javac -encoding UTF-8 -d "$classes" @"$module/target/sources.txt"
fi
output="$(java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -ea -cp "$classes" "$main_class" "$@")"
printf '%s\n' "$output"
grep -Fq "$pass_marker" <<< "$output"

