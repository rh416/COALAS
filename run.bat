@echo off

:: Get the path to the user's Java Installation
set JRE_PATH=
set JDK_PATH=

:: Keep these two blocks outside of if-statements otherwise they don't seem to work!

:: Detect the JRE location
echo Detecting JRE installation location
FOR /F "skip=2 tokens=2*" %%A IN ('REG QUERY "HKLM\Software\JavaSoft\Java Runtime Environment" /v CurrentVersion 2^>nul') DO set CurVer=%%B
FOR /F "skip=2 tokens=2*" %%A IN ('REG QUERY "HKLM\Software\JavaSoft\Java Runtime Environment\%CurVer%" /v JavaHome 2^>nul') DO set JRE_PATH=%%B

:: Detect the JDK location
echo Detecting JDK installation location
FOR /F "skip=2 tokens=2*" %%A IN ('REG QUERY "HKLM\Software\JavaSoft\Java Development Kit" /v CurrentVersion 2^>nul') DO set CurVer=%%B
FOR /F "skip=2 tokens=2*" %%A IN ('REG QUERY "HKLM\Software\JavaSoft\Java Development Kit\%CurVer%" /v JavaHome 2^>nul') DO set JDK_PATH=%%B

:: If the JRE Path wasn't found, use the JDK Path
if [%JRE_PATH%] == [] (
    set JAVA_PATH=%JDK_PATH%
) else (
:: Otherwise, use the JRE Path
    set JAVA_PATH=%JRE_PATH%
)

:: If we still don't have a path to Java, show an error and stop
if [%JAVA_PATH] == [] (
    echo No Java installation found! Please install Java
    :: Exit the script
    GOTO:EOF
) else (
    echo Java Installation found at %JAVA_PATH%
)

PATH %PATH%;%JAVA_PATH%\bin\

:: Detect the version of Java in use (it must be at least 1.7)
FOR /F tokens^=2-5^ delims^=.-_^" %%j in ('java -fullversion 2^>^&1') do set "JAVA_VERSION=%%j%%k%%l%%m"
if %JAVA_VERSION% LSS 17000 (
    echo Please install a Java version 1.7 or later
    :: Exit the script
    GOTO:EOF
    )

:: Detect which version (32 vs 64 bit) of Jav is running

:: Check for 32-bit Java
FOR /F "delims=" %%a in ('java -d32 2^>^&1') do set JAVA_RESPONSE_32=%%a
FOR /F "delims=" %%a in ('java -d64 2^>^&1') do set JAVA_RESPONSE_64=%%a


:: If the JVM is not 32 bit
if "%JAVA_RESPONSE_32%" == "Please install the desired version." (
    :: Use 64 bit platform
    set PLATFORM=windows64
) else (
    :: Otherwise, use 32 bit platform
    set PLATFORM=windows32
)

:: Run the program
"java" -Djava.library.path=lib/serial/%PLATFORM%/ -jar sysiass-wheelchair-gui.jar
pause