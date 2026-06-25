# Fabric JAR Class Injector

This workspace contains a small offline tool that injects a compiled Java class
from one Fabric mod JAR into another Fabric mod JAR and patches
`fabric.mod.json` so Fabric calls it when Minecraft starts.

The sample fixtures use version `26.1.2` in their mod metadata.

## Build and test the sample mods

```powershell
.\scripts\build_and_test_samples.ps1
```

That script creates two sample mods:

- `build/libs/donor-sample-26.1.2.jar`
- `build/libs/target-sample-26.1.2.jar`

Then it injects `com.example.donor.DonorInjectedMod` from the donor JAR into the
target JAR and writes:

- `build/libs/target-sample-26.1.2-injected.jar`

## Use the injector on real Fabric mods

```powershell
python .\tools\inject_fabric_class.py `
  --donor .\path\to\donor.jar `
  --target .\path\to\target.jar `
  --output .\path\to\target-injected.jar `
  --class "com.example.donor.DonorInjectedMod" `
  --include-package `
  --overwrite
```

The injected class must be compiled already and should be a valid Fabric
`ModInitializer` when injected into the default `main` entrypoint.
