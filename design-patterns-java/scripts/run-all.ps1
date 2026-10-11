$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new()
$runner = Join-Path $PSScriptRoot 'run.ps1'
$passed = 0
foreach ($id in 1..21) {
    & $runner ('{0:D2}' -f $id)
    if ($LASTEXITCODE -ne 0) { throw "Chapter $id failed" }
    $passed++
}
Write-Host "[PASS] all: $passed/21 chapters passed"

