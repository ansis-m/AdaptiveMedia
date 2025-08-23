@echo off
setlocal enabledelayedexpansion

set DOCKER_REGISTRY=docker.io
set DOCKER_USERNAME=ansism
set IMAGE_NAME=adaptivemedia-app
set IMAGE_TAG=%1
if "%IMAGE_TAG%"=="" set IMAGE_TAG=latest

set FULL_IMAGE_NAME=%DOCKER_USERNAME%/%IMAGE_NAME%:%IMAGE_TAG%

echo =========================================
echo Building and Publishing Docker Image
echo =========================================
echo Image: %FULL_IMAGE_NAME%
echo.

echo Step 1: Building Spring Boot application...
if exist gradlew.bat (
    call gradlew.bat clean build
) else if exist gradle.bat (
    call gradle.bat clean build
) else (
    echo Error: Gradle not found. Please install Gradle or use Gradle wrapper.
    exit /b 1
)

if %ERRORLEVEL% neq 0 (
    echo Error: Build failed
    exit /b 1
)

echo.
echo Step 2: Building Docker image...
docker build -t %FULL_IMAGE_NAME% .

if %ERRORLEVEL% neq 0 (
    echo Error: Docker build failed
    exit /b 1
)

echo.
echo Step 4: Pushing image to registry...
docker push %FULL_IMAGE_NAME%

if %ERRORLEVEL% neq 0 (
    echo Error: Docker push failed
    exit /b 1
)

if not "%IMAGE_TAG%"=="latest" (
    echo.
    echo Step 5: Tagging and pushing as 'latest'...
    docker tag %FULL_IMAGE_NAME% %DOCKER_USERNAME%/%IMAGE_NAME%:latest
    docker push %DOCKER_USERNAME%/%IMAGE_NAME%:latest
)

echo.
echo =========================================
echo Success! Image published as:
echo   - %FULL_IMAGE_NAME%
if not "%IMAGE_TAG%"=="latest" echo   - %DOCKER_USERNAME%/%IMAGE_NAME%:latest
echo =========================================
echo.
echo To use this image in docker-compose, update the image field to:
echo   image: %FULL_IMAGE_NAME%

endlocal