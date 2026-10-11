param([switch]$JdkOnly)
$ErrorActionPreference = 'Stop'
$runner = Join-Path $PSScriptRoot 'run.ps1'
$passed = 0
foreach ($id in 1..21) {
    if ($JdkOnly) { & $runner ('{0:D2}' -f $id) -JdkOnly }
    else { & $runner ('{0:D2}' -f $id) }
    if ($LASTEXITCODE -ne 0) { throw "Module $id failed" }
    $passed++
}
Write-Host "[PASS] all: $passed/21 modules passed"

