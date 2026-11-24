<#
    Verifie que Consul est bien la source de verite apres migration.
    Usage : .\scripts\verify-migration.ps1
#>
param(
    [string]$Gateway = "http://localhost:8888",
    [string]$Consul = "http://localhost:8500"
)

$script:erreurs = 0
function Check($libelle, $condition) {
    if ($condition) { Write-Host "  [OK]   $libelle" -ForegroundColor Green }
    else { Write-Host "  [KO]   $libelle" -ForegroundColor Red; $script:erreurs++ }
}

Write-Host ""
Write-Host "1. Consul repond ?" -ForegroundColor Cyan
try {
    # Invoke-RestMethod renvoie un objet : on recupere les noms via les proprietes.
    $reponse = Invoke-RestMethod "$Consul/v1/catalog/services" -TimeoutSec 10
    $noms = @($reponse.PSObject.Properties.Name)
    Check "API Consul joignable" $true
} catch {
    Check "API Consul joignable" $false
    $noms = @()
}

Write-Host ""
Write-Host "2. Les services sont-ils declares dans Consul ?" -ForegroundColor Cyan
Check "SERVICE-CLIENT enregistre"  ($noms -contains "SERVICE-CLIENT")
Check "SERVICE-CAR enregistre"     ($noms -contains "SERVICE-CAR")
Check "SERVICE-GATEWAY enregistre" ($noms -contains "SERVICE-GATEWAY")
Check "Aucun service EUREKA"       (-not ($noms | Where-Object { $_ -match "EUREKA" }))

Write-Host ""
Write-Host "3. La Gateway route-t-elle via Consul ?" -ForegroundColor Cyan
try {
    $registre = Invoke-RestMethod "$Gateway/api/migration/registry" -TimeoutSec 10
    Check "Gateway : provider = CONSUL"   ($registre.provider -eq "CONSUL")
    Check "Gateway : au moins 3 services" ($registre.serviceCount -ge 3)
} catch {
    Check "Gateway : /api/migration/registry" $false
}

Write-Host ""
Write-Host "4. Appels de bout en bout" -ForegroundColor Cyan
try {
    $clients = Invoke-RestMethod "$Gateway/SERVICE-CLIENT/api/client" -TimeoutSec 15
    Check "SERVICE-CLIENT via Gateway" (@($clients).Count -gt 0)
} catch { Check "SERVICE-CLIENT via Gateway" $false }

try {
    $cars = Invoke-RestMethod "$Gateway/SERVICE-CAR/api/car" -TimeoutSec 20
    Check "SERVICE-CAR via Gateway (+ jointure client)" (@($cars).Count -gt 0)
} catch { Check "SERVICE-CAR via Gateway (+ jointure client)" $false }

Write-Host ""
if ($script:erreurs -eq 0) { Write-Host "Migration validee : Consul est la source de verite." -ForegroundColor Green }
else { Write-Host "$($script:erreurs) verification(s) en echec." -ForegroundColor Red; exit 1 }