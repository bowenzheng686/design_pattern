$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$chapterRoot = (Resolve-Path -LiteralPath (Join-Path $root 'chapters')).Path
$buildDirs = @(Get-ChildItem -LiteralPath $chapterRoot -Directory | ForEach-Object {
    $candidate = Join-Path $_.FullName 'build'
    if (Test-Path -LiteralPath $candidate) { (Resolve-Path -LiteralPath $candidate).Path }
})
foreach ($dir in $buildDirs) {
    if (-not $dir.StartsWith($chapterRoot, [StringComparison]::OrdinalIgnoreCase)) { throw "Unsafe path: $dir" }
    Remove-Item -LiteralPath $dir -Recurse -Force
}
Write-Host "[PASS] clean: removed $($buildDirs.Count) build directories"

