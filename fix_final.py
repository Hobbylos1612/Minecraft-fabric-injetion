import re, os

SRC_DIR = r"C:\Users\Ansgar\Documents\Minecraft fabric injetion\jooon-reimagined-recomp\src\main\java"

files = []
for root, dirs, fnames in os.walk(SRC_DIR):
    for fn in fnames:
        if fn.endswith('.kt'):
            files.append(os.path.join(root, fn))

print(f"Processing {len(files)} files...")

fixed_count = 0
total_changes = 0

for filepath in files:
    with open(filepath, 'r', encoding='utf-8') as f:
        orig = f.read()
    content = orig

    # 1. replace$default -> replace (simplified - best effort)
    # Pattern: replace$default(str, a, b, ...) -> str.replace(a, b)
    content = re.sub(
        r'(?P<obj>\w+)\.replace\$default\(\s*(?P<obj2>\w+)\s*,\s*"(?P<a>[^"]*)"\s*,\s*"(?P<b>[^"]*)"\s*,\s*(false|true)\s*,\s*\d+\s*,\s*null\s*\)',
        r'\g<obj>.replace("\g<a>", "\g<b>")',
        content
    )
    
    # Also handle replace$default with variables
    content = re.sub(
        r'replace\$default\(([^,]+),\s*([^,]+),\s*([^,]+),\s*(false|true),\s*\d+,\s*null\)',
        r'\1.replace(\2, \3)',
        content
    )

    # 2. constructor_impl -> Result.success (for value construction)
    content = re.sub(r'Result\.constructor_impl\(', r'Result.success(', content)
    # standalone constructor_impl calls
    content = re.sub(r'constructor_impl\(', r'Result.success(', content)
    
    # 3. isFailure_impl -> isFailure
    content = re.sub(r'\.isFailure_impl', r'.isFailure', content)
    
    # 4. ResultKt references -> remove (they're internal)
    content = re.sub(r'\w+ResultKt\.[^;]+\)', ')', content)
    content = re.sub(r'\w+ResultKt\.\w+', '', content)
    
    # 5. Remove @JvmStatic on class-level (not in companion object)
    # Only remove when NOT inside companion object
    lines = content.split('\n')
    new_lines = []
    in_companion = 0
    for line in lines:
        stripped = line.strip()
        if stripped.startswith('companion object') or stripped.startswith('public companion object'):
            in_companion += 1
        elif stripped == '}' and in_companion > 0:
            in_companion -= 1
        
        if '@JvmStatic' in line and in_companion == 0:
            line = line.replace('@JvmStatic', '')
        
        new_lines.append(line)
    content = '\n'.join(new_lines)
    
    # 6. Remove 'final' modifier from getters
    content = re.sub(r'\bfinal get\(\)', r'get()', content)
    content = re.sub(r'final val\b', r'val', content)
    # 'final' before a property accessor
    content = re.sub(r'\bfinal\s+(get|set)\b', r'\1', content)
    
    # 7. Remove 'final' before 'val'/'var' in class body
    content = re.sub(r'\bfinal\s+(val|var)\b', r'\1', content)
    
    # 8. Remove 'public' modifier (Kotlin default)
    content = re.sub(r'\bpublic\s+(fun|val|var|class|interface|object|companion|enum|data|inner|abstract|open|override|inline|suspend)\b', r'\1', content)
    # Also handle 'public' before constructor
    content = re.sub(r'\bpublic\s+constructor', r'constructor', content)
    
    # 9. Fix open fun that should be override (heuristic: if it overrides interface method)
    # We can't detect this reliably without AST, but we can fix 'public open fun' -> 'override fun'
    # for methods named like 'on*' or 'render*' that are likely overrides
    # Actually this is too risky. Skip.

    # 10. val cannot be reassigned -> var (when there's an assignment to a val)
    # This needs careful handling per-file, skip for bulk

    # 11. Remove unused UNUSED imports (not needed but harmless)

    # 12. Data class primary constructor params without val/var (heuristic)
    # Look for data class declaration, then fix constructor params
    # This is complex, skip for now
    
    # 13. One type argument expected -> try to fix common cases
    # Collection -> Collection<*>
    content = re.sub(r'(?<![\w.])(Collection|List|Set|Map|Iterable|Iterator|ArrayList|HashMap|HashSet)(\s*[)>)\]])', r'\1<*>\2', content)
    
    # 14. HandledScreen without type args -> HandledScreen<*>
    content = re.sub(r'(?<!\w)HandledScreen(?!\s*<)', r'HandledScreen<*>', content)
    
    # 15. Throwable type mismatch -> Exception type
    content = re.sub(r'catch\s*\(\s*e\s*:\s*Throwable\s*\)', r'catch (e: Exception)', content)
    content = re.sub(r'catch\s*\(\s*_\s*:\s*Throwable\s*\)', r'catch (_: Exception)', content)
    
    # 16. $this$ references - remove the inlined lambda artifacts
    # Replace $this$xxx with the receiver variable (best effort)
    content = re.sub(r'\$this\$[a-zA-Z_]+', 'this', content)
    # $tmp0 -> temp variable (remove or replace)
    content = re.sub(r'\$tmp\d+', '', content)
    
    # 17. '<set-?>' parameter name -> proper name
    content = content.replace('`<set-?>`', 'value')
    content = content.replace('`<set-?>`', 'value')
    
    # 18. 'val ClassName :' -> needs initialization
    # Not easily fixable in bulk
    
    # 19. Remove empty lines with just `var10000` etc (these are temp vars that don't do anything)
    content = re.sub(r'^\s+val \w+: \w+ = .*?$', '', content, flags=re.MULTILINE)
    
    # 20. Fix lines that are just `variablename` with nothing else (missing return in Kotlin)
    content = re.sub(r'^\s+(\w+)\s*$', r'return \1', content, flags=re.MULTILINE)
    
    if content != orig:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        rp = os.path.relpath(filepath, SRC_DIR)
        print(f"  Fixed: {rp}")
        fixed_count += 1

print(f"\nDone! Fixed {fixed_count} files")
