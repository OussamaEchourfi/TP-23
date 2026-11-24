<#
    Rollback : revenir a l etat Eureka (tag git baseline-eureka).
    Usage : .\scripts\rollback-to-eureka.ps1 [-DryRun]
#>
param([switch]$DryRun)

$racine = Split-Path $PSScriptRoot -Parent
Push-Location $racine
try {
    Write-Host "Etat actuel :" -ForegroundColor Cyan
    git status --short

    if ($DryRun) {
        Write-Host ""
        Write-Host "[DRY-RUN] commandes qui seraient executees :" -ForegroundColor Yellow
        Write-Host "  git checkout baseline-eureka -- client car gateway server_eureka"
        return
    }

    git checkout baseline-eureka -- client car gateway server_eureka
    Write-Host ""
    Write-Host "Retour a l etat Eureka effectue (tag baseline-eureka)." -ForegroundColor Green
    Write-Host "Relancer les services avec le serveur Eureka (port 8761)."
}
finally {
    Pop-Location
}