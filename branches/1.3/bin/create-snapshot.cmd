@echo off

setlocal

set CHK_HOME=_%RAIN_HOME%

if "%CHK_HOME:"=%" == "_" goto HOME_MISSING

"%RAIN_HOME:"=%\bin\rain-cmd" CreateSnapshotCommand %*
goto DONE
:HOME_MISSING
echo RAIN_HOME is not set
exit /b 1

:DONE
