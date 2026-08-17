@echo off
chcp 65001 >nul
echo ============================================
echo   DS1 Backend - Spring Boot
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
echo [INFO] Starting Spring Boot on port 8080...
echo.

cd /d "%~dp0backend"

:: Check if Maven wrapper exists, otherwise use mvn
if exist "mvnw.cmd" (
    call mvnw.cmd spring-boot:run
) else (
    echo [WARN] Maven wrapper not found. Please install Maven and run:
    echo       mvn spring-boot:run
    echo.
    echo Alternatively, open this project in IntelliJ IDEA and run Ds1Application.java
)
pause
