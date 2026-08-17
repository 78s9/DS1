@echo off
chcp 65001 >nul
echo ============================================
echo   DS1 Backend - Build (package to JAR)
echo ============================================
echo.

:: Prefer the bundled JDK 8 if present, otherwise fall back to system Java
if exist "%~dp0jdk1.8.0_202" (
    set "JAVA_HOME=%~dp0jdk1.8.0_202"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
    echo [INFO] Using bundled JDK: %JAVA_HOME%
) else (
    echo [INFO] Bundled JDK not found, using system Java (JDK 8 recommended).
)

cd /d "%~dp0backend"

if not exist "mvnw.cmd" (
    echo [ERROR] mvnw.cmd not found. Install Maven and run: mvn clean package -DskipTests
    pause
    exit /b 1
)

call mvnw.cmd clean package -DskipTests -q
if errorlevel 1 (
    echo [ERROR] Build failed.
    pause
    exit /b 1
)

echo.
echo [INFO] Build success. Output: target\ds1-backend-1.0.0.jar
pause
