$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [Text.UTF8Encoding]::new()
. (Join-Path $PSScriptRoot 'resolve-python.ps1')
$python = Resolve-ProjectPython
$version = & $python -c "import sys; print('.'.join(map(str, sys.version_info[:3])))"
& $python -c "import sys; raise SystemExit(0 if sys.version_info >= (3, 11) else 1)"
if ($LASTEXITCODE -ne 0) { throw "Python 3.11+ required, actual=$version" }
Write-Host "Python: $version"
Write-Host "Executable: $python"
Write-Host "Platform: $(& $python -c 'import platform; print(platform.platform())')"
Write-Host '[PASS] environment'

