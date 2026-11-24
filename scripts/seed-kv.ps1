<#
    Charge les cles du KV Consul utilisees par spring-cloud-starter-consul-config.
    A lancer apres le demarrage de Consul (consul-dev.ps1).
#>
param(
    [string]$ConsulExe = "$env:USERPROFILE\projects\tools\consul\consul.exe",
    [string]$ConsulAddr = "http://localhost:8500"
)

$env:CONSUL_HTTP_ADDR = $ConsulAddr
$racine = Split-Path $PSScriptRoot -Parent

foreach ($service in @("service-client", "service-car", "service-gateway")) {
    $fichier = Join-Path $racine "consul\config\$service\data.yml"
    if (-not (Test-Path $fichier)) { throw "Fichier absent : $fichier" }
    & $ConsulExe kv put "consul/config/$service/data" "@$fichier"
    Write-Host "KV ecrite : consul/config/$service/data" -ForegroundColor Green
}

Write-Host ""
Write-Host "Contenu du KV :" -ForegroundColor Cyan
& $ConsulExe kv get -recurse consul/config