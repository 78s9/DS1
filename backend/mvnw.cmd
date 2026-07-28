@echo off
setlocal enabledelayedexpansion

set "JAVA_HOME=%~dp0..\jdk1.8.0_202"
set "PATH=%JAVA_HOME%\bin;%PATH%"

set "MVNW_REPOURL=https://repo.maven.apache.org/maven2"
set "MAVEN_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-3.6.3-bin"

if not exist "%MAVEN_HOME%" (
    echo [INFO] Downloading Maven 3.6.3...
    mkdir "%MAVEN_HOME%\.." 2>nul
    powershell -Command "& {[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; Invoke-WebRequest -Uri 'https://archive.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.zip' -OutFile '%TEMP%\maven-3.6.3.zip'}"
    powershell -Command "& {Expand-Archive -Path '%TEMP%\maven-3.6.3.zip' -DestinationPath '%MAVEN_HOME%\..' -Force}"
)

set "MAVEN_CMD=%MAVEN_HOME%\apache-maven-3.6.3\bin\mvn.cmd"
if not exist "%MAVEN_CMD%" (
    set "MAVEN_CMD=%MAVEN_HOME%\bin\mvn.cmd"
)

echo [INFO] Using JDK: %JAVA_HOME%
echo [INFO] Starting Spring Boot...
"%MAVEN_CMD%" %*
