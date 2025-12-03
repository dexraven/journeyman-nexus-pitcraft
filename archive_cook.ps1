Write-Host "ðŸ“¦ ARCHIVING COOK..." -ForegroundColor Cyan

$date = Get-Date -Format "yyyy-MM-dd"
$name = Read-Host "Enter a name for this event (e.g. SuperBowl)"
$safeName = $name -replace ' ', '_'
$fileName = "$date-$safeName.zip"
$source = ".\nexus-data"
$dest = ".\archives\$fileName"

Write-Host "==> Stopping services..." -ForegroundColor Yellow
docker-compose stop nexus-api

Write-Host "==> Compressing data to $dest..." -ForegroundColor Yellow
if (Test-Path $source) {
    Compress-Archive -Path $source -DestinationPath $dest -Force
    Write-Host "âœ… DATA SAVED." -ForegroundColor Green
} else {
    Write-Warning "No data folder found to archive."
}

Write-Host "Restarting services? (Y/N)"
$resp = Read-Host
if ($resp -eq 'Y') {
    docker-compose start nexus-api
    Write-Host "ðŸ”¥ Systems back online."
}
Start-Sleep -Seconds 3
