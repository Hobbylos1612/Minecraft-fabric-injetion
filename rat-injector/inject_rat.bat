@echo off
py -3 "%~dp0inject_rat.py" "%~1" --backup --yes
if errorlevel 1 pause
pause
