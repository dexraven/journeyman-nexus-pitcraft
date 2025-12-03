Write-Host "🍖 Setting up JOURNEYMAN NEXUS PITCRAFT (Current Directory)..." -ForegroundColor Cyan

# 1. Create Module Folders (Updated Package Structure)
# Package: com.journeyman.nexus.pitcraft
New-Item -ItemType Directory -Force -Path "backend/src/main/java/com/journeyman/nexus/pitcraft/controller" | Out-Null
New-Item -ItemType Directory -Force -Path "backend/src/main/resources" | Out-Null
New-Item -ItemType Directory -Force -Path "frontend/src/components" | Out-Null
New-Item -ItemType Directory -Force -Path "archives" | Out-Null
New-Item -ItemType Directory -Force -Path "nexus-data" | Out-Null

# 2. Create THE_PROTOCOL.md
$protocolContent = @"
# 🍖 JOURNEYMAN NEXUS PITCRAFT: PROTOCOL

## FRIDAY (IGNITION)
1. Right-click 'launch_nexus.ps1' -> Run with PowerShell
2. Open Command Prompt -> Run 'ngrok http 8080'
3. Check http://localhost:3000

## SUNDAY (SHUTDOWN)
1. Right-click 'archive_cook.ps1' -> Run with PowerShell
2. Run 'docker-compose down'
"@
Set-Content -Path "THE_PROTOCOL.md" -Value $protocolContent -Encoding UTF8

# 3. Create Launch Script (launch_nexus.ps1)
# Note: I included the fix for the previous syntax error here as well
$launchContent = @"
Write-Host "🔥 IGNITING NEXUS..." -ForegroundColor Yellow

# Check for Docker
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Error "Docker Desktop is not running. Please start it first."
    exit 1
}

# Build Backend (Skip tests for speed)
Write-Host "==> Building Backend..." -ForegroundColor Cyan
if (Test-Path "backend\gradlew.bat") {
    Set-Location backend
    .\gradlew.bat clean build -x test
    Set-Location ..
} else {
    Write-Warning "Gradle wrapper not found, skipping build step (relying on Dockerfile)."
}

# Docker Compose
Write-Host "==> Firing up Containers..." -ForegroundColor Cyan
docker-compose down
docker-compose up --build -d

# Status Report
Write-Host "==================================================" -ForegroundColor Yellow
Write-Host "✅ NEXUS ONLINE" -ForegroundColor Green
Write-Host "   - Dashboard:   http://localhost:3000"
Write-Host "   - API Health:  http://localhost:8080/api/system/status"
Write-Host "==================================================" -ForegroundColor Yellow
Start-Sleep -Seconds 5
"@
Set-Content -Path "launch_nexus.ps1" -Value $launchContent -Encoding UTF8

# 4. Create Archive Script (archive_cook.ps1)
$archiveContent = @"
Write-Host "📦 ARCHIVING COOK..." -ForegroundColor Cyan

`$date = Get-Date -Format "yyyy-MM-dd"
`$name = Read-Host "Enter a name for this event (e.g. SuperBowl)"
`$safeName = `$name -replace ' ', '_'
`$fileName = "`$date-`$safeName.zip"
`$source = ".\nexus-data"
`$dest = ".\archives\`$fileName"

Write-Host "==> Stopping services..." -ForegroundColor Yellow
docker-compose stop nexus-api

Write-Host "==> Compressing data to `$dest..." -ForegroundColor Yellow
if (Test-Path `$source) {
    Compress-Archive -Path `$source -DestinationPath `$dest -Force
    Write-Host "✅ DATA SAVED." -ForegroundColor Green
} else {
    Write-Warning "No data folder found to archive."
}

Write-Host "Restarting services? (Y/N)"
`$resp = Read-Host
if (`$resp -eq 'Y') {
    docker-compose start nexus-api
    Write-Host "🔥 Systems back online."
}
Start-Sleep -Seconds 3
"@
Set-Content -Path "archive_cook.ps1" -Value $archiveContent -Encoding UTF8

# 5. Create Docker Compose Template
$dockerContent = @"
version: '3.8'

services:
  nexus-api:
    build: ./backend
    ports: ["8080:8080"]
    volumes:
      - ./nexus-data:/app/data
    environment:
      - SPRING_DATASOURCE_URL=jdbc:h2:file:/app/data/nexusdb;DB_CLOSE_ON_EXIT=FALSE;AUTO_SERVER=TRUE
      - SPRING_APPLICATION_NAME=journeyman-nexus-pitcraft
      - SPRING_THREADS_VIRTUAL_ENABLED=true

  nexus-ui:
    build: ./frontend
    ports: ["3000:3000"]
    depends_on:
      - nexus-api
"@
Set-Content -Path "docker-compose.yml" -Value $dockerContent -Encoding UTF8

Write-Host "✅ SKELETON DEPLOYED to Current Directory." -ForegroundColor Green