# Development Backend Startup Script
# This script starts the backend in development mode with proper configuration

param(
    [switch]$Clean,
    [switch]$SkipTests,
    [string]$Profile = "dev"
)

Write-Host "Starting Library Backend in Development Mode..." -ForegroundColor Green
Write-Host "Profile: $Profile" -ForegroundColor Yellow

# Set working directory to backend
Set-Location -Path "$PSScriptRoot\..\backend"

try {
    # Clean if requested
    if ($Clean) {
        Write-Host "Cleaning project..." -ForegroundColor Yellow
        mvn clean
        if ($LASTEXITCODE -ne 0) {
            throw "Maven clean failed"
        }
    }

    # Compile and package
    Write-Host "Building project..." -ForegroundColor Yellow
    if ($SkipTests) {
        mvn compile -DskipTests
    } else {
        mvn compile
    }
    
    if ($LASTEXITCODE -ne 0) {
        throw "Maven compile failed"
    }

    # Create data directory for H2 database
    $dataDir = ".\data"
    if (!(Test-Path $dataDir)) {
        New-Item -ItemType Directory -Path $dataDir -Force
        Write-Host "Created data directory: $dataDir" -ForegroundColor Green
    }

    # Create logs directory
    $logsDir = ".\logs"
    if (!(Test-Path $logsDir)) {
        New-Item -ItemType Directory -Path $logsDir -Force
        Write-Host "Created logs directory: $logsDir" -ForegroundColor Green
    }

    # Start the application
    Write-Host "Starting Spring Boot application..." -ForegroundColor Green
    Write-Host "H2 Console will be available at: http://localhost:8080/h2-console" -ForegroundColor Cyan
    Write-Host "API will be available at: http://localhost:8080/api" -ForegroundColor Cyan
    Write-Host "Press Ctrl+C to stop the application" -ForegroundColor Yellow
    Write-Host ""

    mvn spring-boot:run -Dspring-boot.run.profiles=$Profile

} catch {
    Write-Host "Error: $_" -ForegroundColor Red
    exit 1
} finally {
    # Return to original directory
    Set-Location -Path $PSScriptRoot
}