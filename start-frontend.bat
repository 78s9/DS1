@echo off
chcp 65001 >nul
echo ============================================
echo   DS1 Frontend - Vue3 + Element Plus
echo ============================================
echo.

cd /d "%~dp0frontend"

if not exist "node_modules" (
    echo [INFO] Installing dependencies (first run)...
    call npm install
) else (
    echo [INFO] Dependencies already installed, skipping npm install.
)

echo.
echo [INFO] Starting dev server on port 3000...
echo [INFO] Proxy /api -^> http://localhost:8080
echo.

call npm run dev
pause
