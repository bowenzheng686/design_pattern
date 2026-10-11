param(
    [Parameter(Position=0, Mandatory=$true)]
    [ValidatePattern('^\d{1,2}$')]
    [string]$Chapter,
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]]$AppArgs
)

$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new()
$root = Split-Path -Parent $PSScriptRoot
$id = '{0:D2}' -f [int]$Chapter
$chapterDir = Get-ChildItem -LiteralPath (Join-Path $root 'chapters') -Directory |
    Where-Object { $_.Name.StartsWith("$id-") } | Select-Object -First 1

if (-not $chapterDir) { throw "Unknown chapter: $id" }
$propertiesPath = Join-Path $chapterDir.FullName 'chapter.properties'
if (-not (Test-Path -LiteralPath $propertiesPath)) { throw "Missing $propertiesPath" }

$properties = @{}
Get-Content -LiteralPath $propertiesPath -Encoding UTF8 | ForEach-Object {
    if ($_ -match '^\s*([^#][^=]*)=(.*)$') { $properties[$matches[1].Trim()] = $matches[2].Trim() }
}
$mainClass = $properties['main.class']
$passMarker = $properties['pass.marker']
if (-not $mainClass -or -not $passMarker) { throw 'chapter.properties requires main.class and pass.marker' }

$buildDir = Join-Path $chapterDir.FullName 'build'
if (Test-Path -LiteralPath $buildDir) { Remove-Item -LiteralPath $buildDir -Recurse -Force }
New-Item -ItemType Directory -Path $buildDir | Out-Null
$sources = @(Get-ChildItem -LiteralPath (Join-Path $chapterDir.FullName 'src') -Recurse -Filter '*.java' -File | ForEach-Object FullName)
if ($sources.Count -eq 0) { throw "No Java sources in $($chapterDir.Name)" }

Write-Host "==> [$id] compile $($chapterDir.Name)"
& javac -encoding UTF-8 -d $buildDir $sources
if ($LASTEXITCODE -ne 0) { throw "Compilation failed: $($chapterDir.Name)" }

Write-Host "==> [$id] run $mainClass"
$output = & java '-Dfile.encoding=UTF-8' '-Dstdout.encoding=UTF-8' '-Dstderr.encoding=UTF-8' -ea -cp $buildDir $mainClass @AppArgs 2>&1
$exitCode = $LASTEXITCODE
$output | ForEach-Object { Write-Host $_ }
if ($exitCode -ne 0) { throw "Execution failed with exit code $exitCode" }
if (($output -join "`n") -notmatch [regex]::Escape($passMarker)) { throw "Missing pass marker: $passMarker" }
