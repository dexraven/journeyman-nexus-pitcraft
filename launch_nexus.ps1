Write-Host "🔥 IGNITING JOURNEYMAN PITCRAFT (WINDOWS)..." -ForegroundColor Yellow

# 1. Check for Docker
if (-not (Get-Command docker -ErrorAction SilentlyContinue)) {
    Write-Error "Docker Desktop is not running. Please start it first."
    exit 1
}

# 2. Build Backend (Gradle Wrapper for Windows)
Write-Host "==> Building Backend..." -ForegroundColor Cyan
Set-Location backend
.\gradlew.bat clean build -x test
Set-Location ..

# 3. Docker Compose
Write-Host "==> Firing up Containers..." -ForegroundColor Cyan
docker-compose down
docker-compose up --build -d

Write-Host "==================================================" -ForegroundColor Yellow
Write-Host "✅ SYSTEM ONLINE" -ForegroundColor Green
Write-Host "   - Dashboard:   http://localhost:3000"
Write-Host "   - API Health:  http://localhost:8080/actuator/health"
Write-Host "==================================================" -ForegroundColor Yellow
Start-Sleep -Seconds 3