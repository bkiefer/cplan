@echo off
set scriptdir=%~dp0

:: cd %scriptdir%\..
call java -Dapp.dir=%scriptdir%\gui -jar %scriptdir%\gui\target\cplan-gui.jar
