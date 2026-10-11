$ErrorActionPreference = 'Stop'
$root = Split-Path -Parent $PSScriptRoot
$chapterRoot = (Resolve-Path -LiteralPath (Join-Path $root 'chapters')).Path
$targets = @(Get-ChildItem -LiteralPath $chapterRoot -Directory | ForEach-Object {
    $candidate = Join-Path $_.FullName 'target'
    if (Test-Path -LiteralPath $candidate) { (Resolve-Path -LiteralPath $candidate).Path }
})
foreach ($target in $targets) {
    if (-not $target.StartsWith($chapterRoot, [StringComparison]::OrdinalIgnoreCase)) { throw "Unsafe path: $target" }
    Remove-Item -LiteralPath $target -Recurse -Force
}
Write-Host "[PASS] clean: removed $($targets.Count) target directories"

