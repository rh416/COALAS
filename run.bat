@echo off
:: Get the platform from the command line - windows32 or windows64
set platform=%1
:: If no platform was given, default to windows32
if [%platform%] == [] (
	echo No platform given, defaulting to windows32
	set platform=windows32
)
:: Adjust this to match the path to your installation of Java
"C:\Program Files (x86)\Java\jre7\bin\java.exe" -Djava.library.path=lib/serial/%platform%/ -jar sysiass-wheelchair-gui.jar
pause