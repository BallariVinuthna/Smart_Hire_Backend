@echo off
set "MAVEN_CMD=C:\Program Files\Apache NetBeans\java\maven\bin\mvn.cmd"
if exist "%MAVEN_CMD%" (
    "%MAVEN_CMD%" %*
) else (
    mvn %*
)
