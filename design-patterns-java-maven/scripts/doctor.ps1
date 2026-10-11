$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [Text.UTF8Encoding]::new()

foreach ($tool in @('java', 'javac')) {
    if (-not (Get-Command $tool -ErrorAction SilentlyContinue)) { throw "Missing command: $tool" }
}
$javacText = (& cmd /c "javac -version 2>&1") -join ' '
if ($javacText -notmatch '(\d+)(?:\.\d+)*' -or [int]$matches[1] -lt 17) {
    throw "JDK 17+ required: $javacText"
}
$maven = Get-Command mvn.cmd,mvn -ErrorAction SilentlyContinue | Select-Object -First 1
Write-Host "Java: $((& cmd /c 'java -version 2>&1' | Select-Object -First 1))"
Write-Host "Compiler: $javacText"
if ($maven) {
    Write-Host "Maven: $($maven.Source)"
    & $maven.Source -version | Select-Object -First 2 | ForEach-Object { Write-Host $_ }
} else {
    Write-Host 'Maven: not found; scripts will use the JDK fallback compiler.'
}
Write-Host '[PASS] environment'

