$ErrorActionPreference = 'Stop'
$root = (Resolve-Path (Split-Path -Parent $PSScriptRoot)).Path
$names = @('__pycache__','.pytest_cache','.ruff_cache','.mypy_cache','build','dist')
$dirs = @(Get-ChildItem -LiteralPath $root -Directory -Recurse -Force | Where-Object {
    $names -contains $_.Name -or $_.Name.EndsWith('.egg-info')
}) | Sort-Object { $_.FullName.Length } -Descending
$removed = 0
foreach ($dir in $dirs) {
    $resolved = $dir.FullName
    if (-not $resolved.StartsWith($root, [StringComparison]::OrdinalIgnoreCase)) { throw "Unsafe path: $resolved" }
    if (Test-Path -LiteralPath $resolved) { Remove-Item -LiteralPath $resolved -Recurse -Force; $removed++ }
}
Write-Host "[PASS] clean: removed $removed cache/build directories"

