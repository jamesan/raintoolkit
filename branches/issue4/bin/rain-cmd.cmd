@echo off

setlocal


set CHK_JAVA_HOME=_%JAVA_HOME%
set CHK_RAIN_HOME=_%RAIN_HOME%

if "%CHK_RAIN_HOME:"=%" == "_" goto RAIN_HOME_MISSING
REM if "%CHK_JAVA_HOME:"=%" == "_" goto JAVA_HOME_MISSING 

SET CP=%CLASSPATH%

SET CP=%CP%;%RAIN_HOME%\lib\activation.jar
SET CP=%CP%;%RAIN_HOME%\lib\cglib-nodep-2.2.jar
SET CP=%CP%;%RAIN_HOME%\lib\cli-10.jar
SET CP=%CP%;%RAIN_HOME%\lib\commons-beanutils-1.8.0.jar
SET CP=%CP%;%RAIN_HOME%\lib\commons-codec-1.3.jar
SET CP=%CP%;%RAIN_HOME%\lib\commons-collections-3.2.1.jar
SET CP=%CP%;%RAIN_HOME%\lib\commons-httpclient-3.1.jar
SET CP=%CP%;%RAIN_HOME%\lib\commons-lang-2.4.jar
SET CP=%CP%;%RAIN_HOME%\lib\commons-logging-1.1.1.jar
SET CP=%CP%;%RAIN_HOME%\lib\ehcache-1.5.0.jar
SET CP=%CP%;%RAIN_HOME%\lib\ejb3-persistence-1.0.2.GA.jar
SET CP=%CP%;%RAIN_HOME%\lib\javassist.jar
SET CP=%CP%;%RAIN_HOME%\lib\jaxb-api.jar
SET CP=%CP%;%RAIN_HOME%\lib\jaxb-impl.jar
SET CP=%CP%;%RAIN_HOME%\lib\jets3t-0.6.1.jar
SET CP=%CP%;%RAIN_HOME%\lib\jsr107cache-1.0.jar
SET CP=%CP%;%RAIN_HOME%\lib\jsr173_1.0_api.jar
SET CP=%CP%;%RAIN_HOME%\lib\scannotation-1.0.2.jar
SET CP=%CP%;%RAIN_HOME%\lib\servlet-api-2.5-6.1.12rc1.jar
SET CP=%CP%;%RAIN_HOME%\lib\typica.jar
SET CP=%CP%;%RAIN_HOME%\lib\xstream-1.3.jar

SET CP=%CP%;%RAIN_HOME%\classes



SET CMD=%1

SET ARGV=%2
SHIFT
SHIFT
:ARGV_LOOP
IF (%1) == () GOTO ARGV_DONE
SET ARG=%1
SET ARG=%ARG:"=%
SET ARGV=%ARGV% "%ARG%"
SHIFT
GOTO ARGV_LOOP
:ARGV_DONE

java %RAIN_JVM_ARGS% -classpath "%CP%" rain.%CMD% %ARGV%
goto DONE

:JAVA_HOME_MISSING
echo JAVA_HOME is not set
exit /b 1

:RAIN_HOME_MISSING
echo RAIN_HOME is not set
exit /b 1

:DONE
endlocal
