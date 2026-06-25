@echo off
setlocal

cd /d "%~dp0"
python "%~dp0tools\injector_gui.py"

if errorlevel 1 (
  echo.
  echo Failed to start the injector GUI.
  pause
)
