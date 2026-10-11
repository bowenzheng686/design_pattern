param(
    [Parameter(Position=0, Mandatory=$true)]
    [ValidatePattern('^\d{1,2}$')]
    [string]$Chapter,
    [switch]$JdkOnly,
    [Parameter(ValueFromRemainingArguments=$true)]
    [string[]]$AppArgs
)

$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [Text.UTF8Encoding]::new()
$root = Split-Path -Parent $PSScriptRoot
$id = '{0:D2}' -f [int]$Chapter
$module = Get-ChildItem -LiteralPath (Join-Path $root 'chapters') -Directory |
    Where-Object { $_.Name.StartsWith("$id-") } | Select-Object -First 1
if (-not $module) { throw "Unknown chapter: $id" }

$properties = @{}
Get-Content -LiteralPath (Join-Path $module.FullName 'module.properties') -Encoding UTF8 | ForEach-Object {
    if ($_ -match '^\s*([^#][^=]*)=(.*)$') { $properties[$matches[1].Trim()] = $matches[2].Trim() }
}
$mainClass = $properties['main.class']; $passMarker = $properties['pass.marker']
if (-not $mainClass -or -not $passMarker) { throw 'module.properties is incomplete' }
$classes = Join-Path $module.FullName 'target\classes'
$maven = Get-Command mvn.cmd,mvn -ErrorAction SilentlyContinue | Select-Object -First 1

if ($maven -and -not $JdkOnly) {
    Write-Host "==> [$id] Maven compile $($module.Name)"
    & $maven.Source -q -f (Join-Path $module.FullName 'pom.xml') package '-DskipTests'
    if ($LASTEXITCODE -ne 0) { throw "Maven build failed: $($module.Name)" }
} else {
    Write-Host "==> [$id] JDK fallback compile $($module.Name)"
    if (Test-Path -LiteralPath $classes) { Remove-Item -LiteralPath $classes -Recurse -Force }
    New-Item -ItemType Directory -Path $classes -Force | Out-Null
    $sources = @(Get-ChildItem -LiteralPath (Join-Path $module.FullName 'src\main\java') -Recurse -Filter '*.java' -File |
        ForEach-Object FullName)
    if ($sources.Count -eq 0) { throw "No Java sources: $($module.Name)" }
    & javac -encoding UTF-8 -d $classes $sources
    if ($LASTEXITCODE -ne 0) { throw "javac failed: $($module.Name)" }
}

Write-Host "==> [$id] run $mainClass"
$output = & java '-Dfile.encoding=UTF-8' '-Dstdout.encoding=UTF-8' '-Dstderr.encoding=UTF-8' -ea -cp $classes $mainClass @AppArgs 2>&1
$exitCode = $LASTEXITCODE
$output | ForEach-Object { Write-Host $_ }
if ($exitCode -ne 0) { throw "Execution failed: $exitCode" }
if (($output -join "`n") -notmatch [regex]::Escape($passMarker)) { throw "Missing pass marker: $passMarker" }

