import os
import re

SRC = r"C:\Users\Ansgar\Documents\Minecraft fabric injetion\jooon-reimagined-recomp\src\main\java\jooon"

def fix_file(filepath):
    with open(filepath, 'r', encoding='utf-8', errors='replace') as f:
        text = f.read()
    
    orig = text
    
    # === FIX 1: Remove duplicate import lines ===
    lines = text.split('\n')
    new_lines = []
    seen_imports = set()
    package_line = None
    for line in lines:
        if line.startswith('package '):
            package_line = line
            new_lines.append(line)
        elif line.startswith('import '):
            # Normalize the import for dedup
            imp = line.strip()
            if imp not in seen_imports:
                seen_imports.add(imp)
                new_lines.append(line)
            # else: skip duplicate
        else:
            new_lines.append(line)
    text = '\n'.join(new_lines)

    # === FIX 2: Initialize properties that have @JvmField and private set ===
    # Pattern: @JvmField\n@Nullable\nvar name: Type?\n   private set
    # -> @JvmField\n@Nullable\nvar name: Type? = null\n   private set
    text = re.sub(
        r'(@JvmField\s*(?:\n\s*@\w+(?:\([^)]*\))?)*\s*\n\s*)(var\s+\w+\s*:\s*(\w+)\?)\s*\n\s*private set',
        r'\1\2 = null\n   private set',
        text
    )
    # Pattern: @Entry(...)  (no @Nullable)
    # @JvmField\nvar name: Boolean\n   private set
    # -> @JvmField\nvar name: Boolean = false\n   private set
    text = re.sub(
        r'(@JvmField\s*\n\s*)(var\s+\w+\s*:\s*Boolean)\s*\n\s*private set',
        r'\1\2 = false\n   private set',
        text
    )
    text = re.sub(
        r'(@JvmField\s*\n\s*)(var\s+\w+\s*:\s*Int)\s*\n\s*private set',
        r'\1\2 = 0\n   private set',
        text
    )
    text = re.sub(
        r'(@JvmField\s*\n\s*)(var\s+\w+\s*:\s*Double)\s*\n\s*private set',
        r'\1\2 = 0.0\n   private set',
        text
    )
    text = re.sub(
        r'(@JvmField\s*\n\s*)(var\s+\w+\s*:\s*Float)\s*\n\s*private set',
        r'\1\2 = 0.0f\n   private set',
        text
    )
    text = re.sub(
        r'(@JvmField\s*\n\s*)(var\s+\w+\s*:\s*Long)\s*\n\s*private set',
        r'\1\2 = 0L\n   private set',
        text
    )
    text = re.sub(
        r'(@JvmField\s*\n\s*)(var\s+\w+\s*:\s*String)\s*\n\s*private set',
        r'\1\2 = ""\n   private set',
        text
    )
    # @JvmField var name: Type (no setter) -> initialize
    text = re.sub(
        r'(@JvmField\s*\n\s*)(var\s+\w+\s*:\s*(\w+)\?\s*)$',
        r'\1\2 = null',
        text
    )

    # === FIX 3: Properties in object/class without init ===
    # var name: Boolean (no setter, no init, in object)
    # This is harder - skip for now as it might change intended semantics
    
    # === FIX 4: var10000 references in method bodies ===
    # Replace var10000 with a local variable declaration where possible
    # This is complex - for now replace usage of undefined var10000 with TODO()
    
    # === FIX 5: Mixin annotations ===
    # @Mixin([SomeClass::class]) -> @Mixin(SomeClass::class) 
    text = re.sub(r'@Mixin\(\[([^\]]+)\]\)', r'@Mixin(\1)', text)
    # @Mixin(SomeClass<*>::class) -> @Mixin(SomeClass::class)
    text = re.sub(r'@Mixin\(([a-zA-Z_][\w.]*)<\*>', r'@Mixin(\1', text)

    # === FIX 6: Remove @Nullable from @JvmField (nullable is expressed via ? in Kotlin) ===
    text = re.sub(r'@Nullable\s*\n\s*(@JvmField)', r'\1', text)

    # === FIX 7: Fix Java-style bytecode access patterns $i$f$ -> remove ===
    text = re.sub(r'\$i\$f\$\w+', '', text)

    # === FIX 8: Fix Java-style `this$0` references in lambdas ===
    text = re.sub(r'`this\$0`', 'this', text)

    # === FIX 9: Remove trailing `;` from Kotlin code (Java-style) ===
    # Only remove semicolons that are NOT in comments
    # Simple approach: remove trailing ; at end of lines (not in strings)
    lines = text.split('\n')
    new_lines = []
    for line in lines:
        stripped = line.rstrip()
        if stripped.endswith(';') and not stripped.startswith('//') and not stripped.startswith('*'):
            # Check it's not a for/while loop with semicolon
            if not re.match(r'^\s*(for|while|if|when)\s*\(', stripped):
                stripped = stripped[:-1]
        new_lines.append(stripped)
    text = '\n'.join(new_lines)

    # === FIX 10: lambda parameter `this$iv` -> regular param name ===
    text = re.sub(r'`\$this\$iv`', 'it', text)

    # === FIX 11: Fix generic imports with <*> ===
    text = re.sub(r'import\s+([a-zA-Z_][\w.]*)<\s*\*\s*>', r'import \1', text)
    text = re.sub(r'import\s+([a-zA-Z_][\w.]*)<\s*(\w+)\s*>', r'import \1', text)
    
    # === FIX 12: access$xxx static methods (decompiler artifact) ===
    text = re.sub(r'access\$\d+\b', 'TODO', text)

    # === FIX 13: Remove @NotNull annotations (implied in Kotlin) ===
    text = re.sub(r'@NotNull\s*\n\s*', '', text)

    # === FIX 14: Remove empty comment blocks that are decompiler crash reports ===
    text = re.sub(r'// Please report this to the Vineflower.*?\n(?://.*?\n)*', '', text)
    text = re.sub(r'/\*\*\s*\n\s*\*\s*Please report.*?\*/', '', text, flags=re.DOTALL)

    # === FIX 15: Fix `open fun MixinScreen(` -> `constructor(` (for mixin constructors) ===
    text = re.sub(r'^\s+open fun ([A-Z]\w+)\s*\(', r'    constructor(', text, flags=re.MULTILINE)

    # === FIX 16: `this` in backticks ===
    text = re.sub(r'`this`', 'this', text)
    text = re.sub(r'`super`', 'super', text)
    text = re.sub(r'`this\d+`', 'this', text)
    
    # === FIX 17: Java-style array initializer {1, 2, 3} inside listOf -> clean up ===
    # listOf(arrayOf(1, 2, 3)) -> listOf(1, 2, 3) - this is valid but ugly, leave it

    # === FIX 18: `this as` casts to types that don't exist ===
    # Can't fix generically

    if text != orig:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(text)
        return True
    return False

fixed = 0
total = 0
for root, dirs, files in os.walk(SRC):
    for fn in files:
        if fn.endswith('.kt'):
            total += 1
            if fix_file(os.path.join(root, fn)):
                fixed += 1

print(f"Scanned {total} files, fixed {fixed}")
