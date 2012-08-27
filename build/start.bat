@echo off
rem Batch file for Starting Helma with a JDK-like virtual machine.

rem To add jar files to the classpath, simply place them into the 
rem dependency directory of this Helma installation.

:: Initialize variables
:: (don't touch this section)
set JAVA_HOME=
set HOP_HOME=
set OPTIONS=

:: Uncomment to set HOP_HOME
rem set HOP_HOME=c:\program files\helma

:: Uncomment to set JAVA_HOME variable
rem set JAVA_HOME=c:\program files\java

:: Uncomment to pass options to the Java virtual machine
rem set JAVA_OPTIONS=-server -Xmx128m

:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::
:::::: No user configuration needed below this line :::::::
:::::::::::::::::::::::::::::::::::::::::::::::::::::::::::

:: Setting the script path
set INSTALL_DIR=%~d0%~p0

:: Using JAVA_HOME variable if defined. Otherwise,
:: Java executable must be contained in PATH variable
if "%JAVA_HOME%"=="" goto default
   set JAVACMD=%JAVA_HOME%\bin\java
   goto end
:default
   set JAVACMD=java
:end

:: Setting HOP_HOME to script path if undefined
if "%HOP_HOME%"=="" (
   set HOP_HOME=%INSTALL_DIR%
)
cd %HOP_HOME%

:: Invoking the Java virtual machine
%JAVACMD% %JAVA_OPTIONS% helma.main.Server %OPTIONS% %*
