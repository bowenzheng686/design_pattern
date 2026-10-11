param(
    [Parameter(Position=0, Mandatory=$true)][ValidatePattern('^\d{1,2}$')][string]$Chapter,
    [Parameter(ValueFromRemainingArguments=$true)][string[]]$AppArgs
)
$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [Text.UTF8Encoding]::new()
. (Join-Path $PSScriptRoot 'resolve-python.ps1')
$python = Resolve-ProjectPython
$root = Split-Path -Parent $PSScriptRoot
& $python (Join-Path $root 'tools\run_chapter.py') $Chapter @AppArgs
if ($LASTEXITCODE -ne 0) { throw "Chapter $Chapter failed" }

