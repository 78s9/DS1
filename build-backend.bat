@echo off
set JAVA_HOME=D:\MyProject\AI MAKE\DS1\jdk1.8.0_202
cd /d "D:\MyProject\AI MAKE\DS1ackend"
call mvnw.cmd clean package -DskipTests -q
