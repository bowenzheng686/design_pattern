$ErrorActionPreference = 'Stop'
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new()
foreach ($tool in @('java', 'javac')) {
    if (-not (Get-Command $tool -ErrorAction SilentlyContinue)) { throw "Missing command: $tool" }
}
$javacText = (& cmd /c "javac -version 2>&1") -join ' '
if ($javacText -notmatch '(\d+)(?:\.\d+)*') { throw "Unknown javac version: $javacText" }
if ([int]$matches[1] -lt 17) { throw "JDK 17+ required, actual: $javacText" }
Write-Host "OS: $([Environment]::OSVersion)"
$javaText = (& cmd /c "java -version 2>&1") | Select-Object -First 1
Write-Host "Java: $javaText"
Write-Host "Compiler: $javacText"
Write-Host '[PASS] environment'
