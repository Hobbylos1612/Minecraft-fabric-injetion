import os
import re

SRC = r"C:\Users\Ansgar\Documents\Minecraft fabric injetion\jooon-reimagined-recomp\src\main\java\jooon"

def fix_file(filepath):
    with open(filepath, 'r', encoding='utf-8', errors='replace') as f:
        text = f.read()
    
    orig = text
    
    # Fix 1: import ... SomeClass<*> -> import ... SomeClass
    text = re.sub(r'import\s+([a-zA-Z_][\w.]*)<(\*|\s*\?\s*)>\s*', r'import \1\n', text)
    
    # Fix 2: @Mixin([SomeClass::class]) -> @Mixin(SomeClass::class)
    text = re.sub(r'@Mixin\(\[([^\]]+)\]\)', r'@Mixin(\1)', text)
    
    # Fix 3: @Mixin(SomeClass<*>::class) -> @Mixin(SomeClass::class)
    text = re.sub(r'@Mixin\(([a-zA-Z_][\w.]*)<\*>', r'@Mixin(\1', text)
    
    # Fix 4: HandledScreen<*> in annotations
    text = re.sub(r'(HandledScreen|Screen|Packet)<\*>', r'\1', text)
    
    # Fix 5: return break -> break (invalid return+break combo)
    text = text.replace('return break', 'break')
    
    # Fix 6: contains(var, str, bool) -> var.contains(str, bool) (only when var looks like a variable)
    # This is a risky fix but let's try it for common patterns
    text = re.sub(
        r'contains\((\s*(?:var\d+|this\$iv|`[^`]+`|[a-zA-Z]\w*)\s*),\s*("[^"]*"|[a-zA-Z]\w*)\s*,\s*(true|false)\s*\)',
        r'\1.contains(\2, \3)',
        text
    )
    
    # Fix 7: `this` in method calls -> this (remove backticks from this/super)
    text = re.sub(r'`this`', 'this', text)
    text = re.sub(r'`super`', 'super', text)
    
    # Fix 8: .class on Java types -> ::class.java
    text = re.sub(r'(java\.lang\.\w+)\.class', r'\1::class.java', text)
    text = re.sub(r'(org\.apache\.\w+)\.class', r'\1::class.java', text)
    # Generic .class reference (e.g. HttpEntity.class)
    text = re.sub(r'(\w+)\.class\b', r'\1::class.java', text)
    
    # Fix 9: CloseableKt.closeFinally(var, var) -> var.close()
    text = re.sub(r'CloseableKt\.closeFinally\((\w+),\s*\w+\)', r'\1.close()', text)
    
    # Fix 10: collectionSizeOrDefault -> replace with count().coerceAtLeast()
    text = re.sub(r'collectionSizeOrDefault\(([^,]+),\s*(\d+)\)', r'\1.count().coerceAtLeast(\2)', text)
    
    # Fix 11: joinToString$default(list, ...) -> list.joinToString("")
    text = re.sub(
        r'joinToString\$default\(\s*([^,]+)\s*,\s*"[^"]*"\s*,\s*null\s*,\s*null\s*,\s*0\s*,\s*null\s*,\s*null\s*,\s*\d+\s*,\s*null\s*\)',
        r'\1.joinToString("")',
        text
    )
    
    # Fix 12: `$this$iv` params in function signatures -> remove
    text = re.sub(r'`\$this\$[^`]+`', 'this', text)
    
    # Fix 13: open fun constructor -> constructor (mixin constructors)
    text = re.sub(r'open fun ([A-Z]\w+)\s*\(', r'constructor(', text)
    
    # Fix 14: var SomeClass; with missing init -> add init
    # Find var/val declarations that lack initialization
    # This is tricky - skip for now
    
    # Fix 15: Remove obfuscated method names (sSsSsSsS pattern)
    # These are ProGuard-obfuscated names that won't compile if referenced elsewhere
    # We can't remove methods, but we can flag them
    
    # Fix 16: `this3`, `this4` etc -> this
    text = re.sub(r'`this\d+`', 'this', text)
    
    # Fix 17: var var10000 -> different fix
    # Many var10000 references are variables that should be `this` or something else
    # Can't auto-fix universally, but can remove type annotations that cause issues
    
    # Fix 18: `$this$` params in function type -> remove
    text = re.sub(r'\$this\$[a-zA-Z_]+', '', text)
    
    # Fix 19: HandledScreen<*> in type annotations
    text = re.sub(r'(as|is)\s+HandledScreen<\*>', r'\1 HandledScreen', text)
    
    # Fix 20: @Inject(method = ["..."], at = [@At("...")]) is valid Kotlin for annotation arrays
    # But @At might need different syntax. Let's check: @At("HEAD") is valid.
    # Actually the Mixin annotations use Kotlin array syntax which IS correct.
    # But @Mixin([...]) is wrong - already fixed above.
    
    # Fix 21: $i$f$xxx artifacts
    text = re.sub(r'\$i\$f\$[a-zA-Z_]+', '', text)
    
    # Fix 22: .toChar().intValue() -> .toInt().toChar()
    text = re.sub(r'\.toChar\(\)\.intValue\(\)', '.toInt().toChar().code', text)
    
    # Fix 23: (Number).toChar().intValue() -> (Number).toInt().toChar().code  
    text = re.sub(r'\(\((\w+)\)\s+as\s+java\.lang\.Number\)\.toChar\(\)\.intValue\(\)', r'(\1).toInt().toChar()', text)
    
    # Fix 24: super.<init> -> super (constructor call)
    text = re.sub(r'super\.<init>', 'super', text)
    
    # Fix 25: @get:JvmName -> @get:JvmName (valid, but check for @set:JvmName)
    
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
