$ErrorActionPreference = "Stop"

$Root = Split-Path -Parent $PSScriptRoot
$Build = Join-Path $Root "build"
$StubClasses = Join-Path $Build "stub-classes"
$DonorClasses = Join-Path $Build "donor-classes"
$TargetClasses = Join-Path $Build "target-classes"
$TestClasses = Join-Path $Build "test-classes"
$Libs = Join-Path $Build "libs"

Remove-Item -LiteralPath $Build -Recurse -Force -ErrorAction SilentlyContinue
New-Item -ItemType Directory -Force -Path $StubClasses, $DonorClasses, $TargetClasses, $TestClasses, $Libs | Out-Null

javac -d $StubClasses (Join-Path $Root "samples/stub/src/net/fabricmc/api/ModInitializer.java")
javac -cp $StubClasses -d $DonorClasses (Join-Path $Root "samples/donor/src/com/example/donor/DonorInjectedMod.java")
javac -cp $StubClasses -d $TargetClasses (Join-Path $Root "samples/target/src/com/example/target/TargetMod.java")

Copy-Item (Join-Path $Root "samples/donor/fabric.mod.json") (Join-Path $DonorClasses "fabric.mod.json")
Copy-Item (Join-Path $Root "samples/target/fabric.mod.json") (Join-Path $TargetClasses "fabric.mod.json")

jar --create --file (Join-Path $Libs "donor-sample-26.1.2.jar") -C $DonorClasses .
jar --create --file (Join-Path $Libs "target-sample-26.1.2.jar") -C $TargetClasses .

python (Join-Path $Root "tools/inject_fabric_class.py") `
  --donor (Join-Path $Libs "donor-sample-26.1.2.jar") `
  --target (Join-Path $Libs "target-sample-26.1.2.jar") `
  --output (Join-Path $Libs "target-sample-26.1.2-injected.jar") `
  --class "com.example.donor.DonorInjectedMod" `
  --overwrite

javac -cp $StubClasses -d $TestClasses (Join-Path $Root "tests/EntrypointSmokeTest.java")
java -cp "$TestClasses;$StubClasses" EntrypointSmokeTest (Join-Path $Libs "target-sample-26.1.2-injected.jar")

python -m zipfile --list (Join-Path $Libs "target-sample-26.1.2-injected.jar")
