#!/usr/bin/env bash
set -euo pipefail
chapter="${1:?usage: ./scripts/run.sh CHAPTER [args...]}"; shift
printf -v id '%02d' "$((10#$chapter))"
root="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
chapter_dir="$(find "$root/chapters" -maxdepth 1 -type d -name "$id-*" -print -quit)"
[[ -n "$chapter_dir" ]] || { echo "Unknown chapter: $id" >&2; exit 1; }
main_class="$(sed -n 's/^main.class=//p' "$chapter_dir/chapter.properties")"
pass_marker="$(sed -n 's/^pass.marker=//p' "$chapter_dir/chapter.properties")"
rm -rf "$chapter_dir/build"; mkdir -p "$chapter_dir/build"
find "$chapter_dir/src" -name '*.java' -print > "$chapter_dir/build/sources.txt"
echo "==> [$id] compile $(basename "$chapter_dir")"
javac -encoding UTF-8 -d "$chapter_dir/build" @"$chapter_dir/build/sources.txt"
echo "==> [$id] run $main_class"
output="$(java -Dfile.encoding=UTF-8 -Dstdout.encoding=UTF-8 -Dstderr.encoding=UTF-8 -ea -cp "$chapter_dir/build" "$main_class" "$@")"
printf '%s\n' "$output"
grep -Fq "$pass_marker" <<< "$output"
