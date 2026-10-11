function Resolve-ProjectPython {
    if ($env:PYTHON -and (Test-Path -LiteralPath $env:PYTHON)) { return $env:PYTHON }
    $local = Join-Path (Split-Path -Parent $PSScriptRoot) '.venv\Scripts\python.exe'
    if (Test-Path -LiteralPath $local) { return $local }
    $command = Get-Command python.exe,python -ErrorAction SilentlyContinue | Select-Object -First 1
    if ($command) { return $command.Source }
    $bundled = Join-Path $env:USERPROFILE '.cache\codex-runtimes\codex-primary-runtime\dependencies\python\python.exe'
    if (Test-Path -LiteralPath $bundled) { return $bundled }
    throw 'Python 3.11+ was not found. Set the PYTHON environment variable or install Python.'
}

