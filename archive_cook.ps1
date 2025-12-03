Write-Host "📦 INITIALIZING COOK ARCHIVE SEQUENCE..." -ForegroundColor Cyan

# 1. Get Metadata
$currentDate = Get-Date -Format "yyyy-MM-dd"
$cookName = Read-Host "Enter a name for this event (e.g. SuperBowl_2025)"
$safeName = $cookName -replace ' ', '_'
$archiveName = "$currentDate-$safeName.zip"
$archiveDir = ".\archives"

# Ensure archive directory exists
if (-not (Test-Path $archiveDir)) {
    New-Item -ItemType Directory -Path $archiveDir | Out-Null
}

# 2. Stop Services (Safe Data)
Write-Host "==> Stopping services to ensure data integrity..." -ForegroundColor Yellow
docker-compose stop pitcraft-api

# 3. Compress using Native PowerShell
Write-Host "==> Compressing data..." -ForegroundColor Cyan
$sourcePath = ".\pitcraft-data\*"
$destPath = "$archiveDir\$archiveName"

try {
    Compress-Archive -Path $sourcePath -DestinationPath $destPath -Force
    Write-Host "✅ SUCCESS: Data saved to $destPath" -ForegroundColor Green
}
catch {
    Write-Error "❌ ERROR: Compression failed. $_"
    exit 1
}

# 4. Restart Option
$response = Read-Host "Do you want to restart the app? (y/n)"
if ($response -eq 'y') {
    docker-compose start pitcraft-api
    Write-Host "🔥 Systems back online." -ForegroundColor Green
} else {
    Write-Host "😴 Systems remaining offline. Goodnight." -ForegroundColor Cyan
}