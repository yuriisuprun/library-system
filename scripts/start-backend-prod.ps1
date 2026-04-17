# Production Backend Startup Script
# This script starts the backend in production mode

param(
    [switch]$Build,
    [string]$JarPath = ""
)

Write-Host "Starting Library Backend in Production Mode..." -ForegroundColor Green

# Set working directory to backend
Set-Location -Path "$PSScriptRoot\..\backend"

try {
    # Build if requested or if no jar path specified
    if ($Build -or [string]::IsNullOrEmpty($JarPath)) {
        Write-Host "Building production JAR..." -ForegroundColor Yellow
        mvn clean package -DskipTests -Pprod
        
        if ($LASTEXITCODE -ne 0) {
            throw "Maven build failed"
        }
        
        $JarPath = ".\target\library-backend-0.0.1-SNAPSHOT.jar"
    }

    # Verify JAR exists
    if (!(Test-Path $JarPath)) {
        throw "JAR file not found: $JarPath"
    }

    # Create production directories
    $prodDataDir = "C:\opt\library\data"
    $prodLogsDir = "C:\opt\library\logs"
    
    if (!(Test-Path $prodDataDir)) {
        New-Item -ItemType Directory -Path $prodDataDir -Force
        Write-Host "Created production data directory: $prodDataDir" -ForegroundColor Green
    }
    
    if (!(Test-Path $prodLogsDir)) {
        New-Item -ItemType Directory -Path $prodLogsDir -Force
        Write-Host "Created production logs directory: $prodLogsDir" -ForegroundColor Green
    }

    # Start the application
    Write-Host "Starting Spring Boot application in production mode..." -ForegroundColor Green
    Write-Host "API will be available at: http://localhost:8080/api" -ForegroundColor Cyan
    Write-Host "Management endpoints at: http://localhost:8081/actuator" -ForegroundColor Cyan
    Write-Host "Logs will be written to: $prodLogsDir\library.log" -ForegroundColor Cyan
    Write-Host "Press Ctrl+C to stop the application" -ForegroundColor Yellow
    Write-Host ""

    java -jar $JarPath --spring.profiles.active=prod

} catch {
    Write-Host "Error: $_" -ForegroundColor Red
    exit 1
} finally {
    # Return to original directory
    Set-Location -Path $PSScriptRoot
}