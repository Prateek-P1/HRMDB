@REM Maven Wrapper startup batch script
@REM Adapted for HRMS project
@echo off
setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
set MAVEN_CMD_LINE_ARGS=%*

for %%i in ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar") do set WRAPPER_JAR=%%~fi

if exist "%WRAPPER_JAR%" (
    java -jar "%WRAPPER_JAR" %MAVEN_CMD_LINE_ARGS%
) else (
    echo Maven wrapper not found. Please run setup first.
    exit /b 1
)
