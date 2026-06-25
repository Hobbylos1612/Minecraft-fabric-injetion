$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent $MyInvocation.MyCommand.Path
python (Join-Path $Root "tools\injector_gui.py")
