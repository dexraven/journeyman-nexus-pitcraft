Write-Host "ðŸ”¥ IGNITING NEXUS..." -ForegroundColor Yellow

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
Write-Host "âœ… NEXUS ONLINE" -ForegroundColor Green
Write-Host "   - Dashboard:   http://localhost:3000"
Write-Host "   - API Health:  http://localhost:8080/api/system/status"
Write-Host "==================================================" -ForegroundColor Yellow
Start-Sleep -Seconds 5
