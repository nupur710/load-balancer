@echo off

set "JAR_PATH=target\load-balancer-1.0-SNAPSHOT-jar-with-dependencies.jar"

echo Checking JAR at: "%JAR_PATH%"

where java >nul 2>nul
if %ERRORLEVEL% NEQ 0 (
    echo Java is not installed. Please install Java and try again.
    exit /b 1
)

if not exist "%JAR_PATH%" (
    echo JAR file not found at: "%JAR_PATH%". Please build the project first.
    exit /b 1
)

java -jar "%JAR_PATH%"
