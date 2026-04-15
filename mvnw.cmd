@REM Maven Wrapper startup batch script
@REM Adapted for HRMS project
@echo off
setlocal

set MAVEN_PROJECTBASEDIR=%~dp0
if "%MAVEN_PROJECTBASEDIR:~-1%"=="\" set MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR:~0,-1%
set MAVEN_CMD_LINE_ARGS=%*

for %%i in ("%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar") do set WRAPPER_JAR=%%~fi

if not "%MAVEN_WRAPPER_DEBUG%"=="" (
    echo [mvnw.cmd] MAVEN_PROJECTBASEDIR=%MAVEN_PROJECTBASEDIR%
    echo [mvnw.cmd] WRAPPER_JAR=%WRAPPER_JAR%
    echo [mvnw.cmd] MAVEN_CMD_LINE_ARGS=%MAVEN_CMD_LINE_ARGS%
)

if exist "%WRAPPER_JAR%" (
    java -cp "%WRAPPER_JAR%" "-Dmaven.multiModuleProjectDirectory=%MAVEN_PROJECTBASEDIR%" org.apache.maven.wrapper.MavenWrapperMain %MAVEN_CMD_LINE_ARGS%
) else (
    echo Maven wrapper not found. Please run setup first.
    exit /b 1
)
