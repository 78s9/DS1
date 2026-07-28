@echo off
chcp 65001 >nul
echo ============================================
echo   DS1 Frontend - Vue3 + Element Plus
echo ============================================
echo.

cd /d "%~dp0frontend"

echo [INFO] Installing dependencies...
call npm install

echo.
echo [INFO] Starting dev server on port 3000...
echo [INFO] Proxy /api -^> http://localhost:8080
echo.

call npm run dev
pause
