$origDir = "C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\src\jooon"
$buildDir = "C:\Users\Ansgar\Documents\Minecraft fabric injetion\jooon-reimagined-recomp\src\main\java\jooon"

# Step 1: Re-copy original files (excluding $inlined)
Get-ChildItem -Recurse -Filter "*.kt" $origDir | ForEach-Object {
    $relPath = $_.FullName.Substring($origDir.Length).TrimStart("\")
    $dest = Join-Path $buildDir $relPath
    $destDir = Split-Path $dest -Parent
    if (-not (Test-Path $destDir)) { New-Item -ItemType Directory -Path $destDir -Force | Out-Null }
    Copy-Item $_.FullName $dest -Force
}
Write-Output "Copied files from JOON RECOMP"

# Delete $inlined files again
Get-ChildItem -Recurse -Filter "*`$*" $buildDir | Remove-Item -Force
Write-Output "Removed generated files"

# Step 2: Apply SAFE bulk fixes
$fixed = 0
Get-ChildItem -Recurse -Filter "*.kt" $buildDir | ForEach-Object {
    $content = Get-Content $_.FullName -Raw
    $orig = $content

    # Remove @SourceDebugExtension annotation lines
    $content = $content -replace '(?m)^\s*import kotlin\.jvm\.internal\.SourceDebugExtension\s*\r?\n', ''
    $content = $content -replace '(?m)^\s*@SourceDebugExtension\(.*?^\)\]\)\s*\r?\n', ''
    $content = $content -replace '(?s)@SourceDebugExtension\(\["SMAP.*?"E"\]\)\s*\r?\n', ''

    # Fix java.lang.* prefixes (safe: exact class names)
    $content = $content -replace '(?<![.\w])java\.lang\.((?:Double|Float|Integer|Boolean|Long|String|Math|Void|Short|Byte|Character))(?![.\w])', '$1'

    # Fix Kotlin stdlib prefixes
    $content = $content -replace '(?<![.\w])CollectionsKt\.(?![.\w])', ''
    $content = $content -replace '(?<![.\w])RangesKt\.(?![.\w])', ''
    $content = $content -replace '(?<![.\w])SequencesKt\.(?![.\w])', ''
    $content = $content -replace '(?<![.\w])MathKt\.(?![.\w])', ''
    $content = $content -replace '(?<![.\w])StringsKt\.(?![.\w])', ''
    $content = $content -replace '(?<![.\w])MapsKt\.(?![.\w])', ''
    $content = $content -replace '(?<![.\w])ArraysKt\.(?![.\w])', ''
    $content = $content -replace '(?<![.\w])TuplesKt\.(?![.\w])', ''
    $content = $content -replace '(?<![.\w])LazyKt\.(?![.\w])', ''
    $content = $content -replace '(?<![.\w])IntrinsicsKt\.(?![.\w])', ''
    $content = $content -replace '(?<![.\w])Intrinsics\.(?![.\w])', ''
    $content = $content -replace '(?<![.\w])JvmPrimitiveKt\.(?![.\w])', ''
    $content = $content -replace '(?<![.\w])ExceptionsKt\.(?![.\w])', ''
    $content = $content -replace '(?<![.\w])KotlinVersion\.(?![.\w])', ''
    $content = $content -replace '(?<![.\w])ReflectionKt\.(?![.\w])', ''

    # TuplesKt.to( -> Pair(
    $content = $content -replace 'TuplesKt\.to\(', 'Pair('

    # INSTANCE. without preceding dot or word
    $content = $content -replace '(?<![.\w])INSTANCE\.(?![.\w])', ''

    # size() on collections -> size
    $content = $content -replace '(?<=[a-zA-Z])size\(\)', 'size'

    # Fix common obfuscated names
    $content = $content -replace '(?<![.\w])field_1350(?![.\w])', 'z'
    $content = $content -replace '(?<![.\w])field_1351(?![.\w])', 'y'
    $content = $content -replace '(?<![.\w])field_1352(?![.\w])', 'x'
    $content = $content -replace '(?<![.\w])field_1353(?![.\w])', 'z'

    if ($content -ne $orig) {
        Set-Content $_.FullName $content -NoNewline
        $fixed++
    }
}
Write-Output "Fixed $fixed files (safe transformations)"
