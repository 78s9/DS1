@echo off
chcp 65001 >nul
echo ============================================
echo   DS1 Backend - Spring Boot
echo ============================================
echo.

set JAVA_HOME=%~dp0jdk1.8.0_202
set PATH=%JAVA_HOME%\bin;%PATH%

echo [INFO] Using JDK: %JAVA_HOME%
echo [INFO] Starting Spring Boot on port 8080...
echo.

cd /d "%~dp0backend"

:: Check if Maven wrapper exists, otherwise use mvn
if exist "mvnw.cmd" (
    call mvnw.cmd spring-boot:run
) else (
    echo [WARN] Maven not found. Please install Maven or use:
    echo       mvn spring-boot:run
    echo.
    echo Alternatively, open this project in IntelliJ IDEA and run Ds1Application.java
)
pause
