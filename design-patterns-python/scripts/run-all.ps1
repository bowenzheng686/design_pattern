$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [Text.UTF8Encoding]::new()
. (Join-Path $PSScriptRoot 'resolve-python.ps1')
$python = Resolve-ProjectPython
$root = Split-Path -Parent $PSScriptRoot
& $python (Join-Path $root 'tools\run_all.py')
if ($LASTEXITCODE -ne 0) { throw 'Full validation failed' }

