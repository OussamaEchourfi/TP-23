<#
    Demarre Consul en mode developpement (etape 2 du TP 23).
    Usage :  .\scripts\consul-dev.ps1
    UI    :  http://localhost:8500/ui/dc1/services
#>
param(
    [string]$ConsulExe = "$env:USERPROFILE\projects\tools\consul\consul.exe",
    [int]$HttpPort = 8500
)

if (-not (Test-Path $ConsulExe)) {
    throw "consul.exe introuvable : $ConsulExe (telechargement : https://developer.hashicorp.com/consul/downloads)"
}

Write-Host "Demarrage de Consul (mode -dev) sur le port $HttpPort ..." -ForegroundColor Cyan
Write-Host "UI : http://localhost:$HttpPort/ui/dc1/services" -ForegroundColor Green

$dataDir = Join-Path $PSScriptRoot "..\consul-data"
New-Item -ItemType Directory -Force -Path $dataDir | Out-Null

& $ConsulExe agent -dev -ui -client=127.0.0.1 -http-port=$HttpPort -data-dir="$dataDir"