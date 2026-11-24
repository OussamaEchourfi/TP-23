<#
    Lance la pile migree en local (sans Docker) :
      Consul (dev) -> seed KV -> client -> car -> gateway
#>
param(
    [string]$JavaHome = "$env:USERPROFILE\.jdks\jdk-21.0.13+9",
    [string]$ConsulExe = "$env:USERPROFILE\projects\tools\consul\consul.exe"
)

$env:JAVA_HOME = $JavaHome
$racine = Split-Path $PSScriptRoot -Parent

Write-Host "1/2 - Consul" -ForegroundColor Cyan
Start-Process powershell -ArgumentList "-NoExit", "-File", (Join-Path $PSScriptRoot "consul-dev.ps1"), "-ConsulExe", $ConsulExe
Start-Sleep -Seconds 8

Write-Host "2/2 - Seed du KV" -ForegroundColor Cyan
& (Join-Path $PSScriptRoot "seed-kv.ps1") -ConsulExe $ConsulExe

foreach ($module in @("client", "car", "gateway")) {
    Write-Host "Demarrage de $module ..." -ForegroundColor Cyan
    Start-Process powershell -ArgumentList "-NoExit", "-Command", "cd '$racine\$module'; mvn -q spring-boot:run"
    Start-Sleep -Seconds 4
}

Write-Host ""
Write-Host "Stack lancee. Verifiez :" -ForegroundColor Green
Write-Host "  Consul UI : http://localhost:8500/ui/dc1/services"
Write-Host "  Gateway   : http://localhost:8888/api/migration/registry"