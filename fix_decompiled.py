"""Fix decompiled Kotlin patterns across the build directory."""
import re
import os
from pathlib import Path

BUILD_DIR = Path(r"C:\Users\Ansgar\Documents\Minecraft fabric injetion\jooon-reimagined-recomp\src\main\java\jooon")

def fix_file(path):
    with open(path, 'r', encoding='utf-8', errors='replace') as f:
        text = f.read()
    orig = text

    # 1. Fix Java casts: (double)value -> (value).toDouble(), etc.
    text = re.sub(r'\(double\)\s*\(([^)]+)\)', r'(\1).toDouble()', text)
    text = re.sub(r'\(float\)\s*\(([^)]+)\)', r'(\1).toFloat()', text)
    text = re.sub(r'\(int\)\s*\(([^)]+)\)', r'(\1).toInt()', text)
    text = re.sub(r'\(long\)\s*\(([^)]+)\)', r'(\1).toLong()', text)
    text = re.sub(r'\(boolean\)\s*\(([^)]+)\)', r'(\1).toBoolean()', text)
    # Also handle (double)value without parens
    text = re.sub(r'\(double\)\s+(\w+)', r'(\1).toDouble()', text)
    text = re.sub(r'\(float\)\s+(\w+)', r'(\1).toFloat()', text)
    text = re.sub(r'\(int\)\s+(\w+)', r'(\1).toInt()', text)
    text = re.sub(r'\(long\)\s+(\w+)', r'(\1).toLong()', text)

    # 2. Fix INSTANCE references
    text = re.sub(r'(\w+)\.INSTANCE\b', r'\1', text)

    # 3. Fix collectionSizeOrDefault -> coerceAtLeast
    text = re.sub(r'collectionSizeOrDefault\(([^,]+),\s*(\d+)\)', r'(\1).coerceAtLeast(\2)', text)

    # 4. Fix @JvmField on properties with custom accessors (remove annotation)
    # Pattern: @JvmField on a property declaration line followed by get/set
    text = re.sub(r'(@JvmField)\s*\n\s*(public|private|internal|protected)?\s*(var|val)\s+\w+\s*[;:].*\n\s*(public|private|internal|protected)?.*\b(get|set)\b',
                  r'\2 \3 \4...', text)  # too complex for regex

    # Simpler approach: remove @JvmField annotations
    text = text.replace('@JvmField\n', '')
    text = text.replace('@JvmField ', '')
    text = re.sub(r'@kotlin\.jvm\.JvmStatic\s*', '', text)

    # 5. Fix Too many arguments for hashCode - remove params from hashCode()
    text = re.sub(r'fun hashCode\([^)]*\):\s*Int\s*\{', 'fun hashCode(): Int {', text)

    # 6. Fix primitive type references: `double` -> `Double` when used as type
    # Replace `: double` with `: Double` but not in variable names
    text = re.sub(r'(?<!\w):\s*double\b(?!\w)', ': Double', text)
    text = re.sub(r'(?<!\w):\s*float\b(?!\w)', ': Float', text)
    text = re.sub(r'(?<!\w):\s*int\b(?!\w)', ': Int', text)
    text = re.sub(r'(?<!\w):\s*boolean\b(?!\w)', ': Boolean', text)
    text = re.sub(r'(?<!\w):\s*long\b(?!\w)', ': Long', text)
    text = re.sub(r'(?<!\w):\s*char\b(?!\w)', ': Char', text)

    # Also fix usage of java.util types used without args
    text = re.sub(r'(?<!\w)java\.util\.Iterator\b(?!\w)', 'java.util.Iterator<*>', text)
    text = re.sub(r'(?<!\w)java\.util\.Collection\b(?!\w)', 'java.util.Collection<*>', text)

    # 7. Fix `val` that is reassigned -> `var`
    # Find blocks where a val is assigned and then reassigned later
    # This is hard with regex, so skip for now

    # 8. Fix data class constructors without val/var
    lines = text.split('\n')
    new_lines = []
    i = 0
    while i < len(lines):
        line = lines[i]
        # Match data class definition line with constructor params
        m = re.match(r'^(\s*)data\s+(class|object)\s+(\w+)\s*\(([^)]*)\)\s*(?:\{|$)', line)
        if m:
            indent = m.group(1)
            class_type = m.group(2)
            class_name = m.group(3)
            params_str = m.group(4).strip()
            if class_type == 'class' and params_str:
                params = [p.strip() for p in params_str.split(',')]
                fixed_params = []
                for p in params:
                    if p and not re.match(r'^(val|var|override|abstract|open)\b', p):
                        # Check if it's a simple "name: Type" without val/var
                        p2 = re.sub(r'=.*', '', p).strip()  # remove default value
                        if re.match(r'^\w+\s*:\s*\w', p2):
                            p = 'val ' + p
                    fixed_params.append(p)
                fixed_params_str = ', '.join(fixed_params)
                if fixed_params_str != params_str:
                    line = f'{indent}data class {class_name}({fixed_params_str})'
        new_lines.append(line)
        i += 1
    text = '\n'.join(new_lines)

    # 9. Fix Java-style iteration to Kotlin for loops
    # Pattern 1: for-each style: val iter = list.iterator()  while(iter.hasNext()) { val item = iter.next() as Type }
    text = re.sub(
        r'val\s+\w+\s*:\s*(?:java\.util\.)?Iterator[^=]*=\s*(\w+)\.iterator\(\)\s*\n\s*while\s*\(\s*\w+\.hasNext\(\)\s*\)\s*\{(.*?)val\s+(\w+)\s*=\s*\w+\.next\(\)(?:\s+as\s+(\w+))?',
        r'for (\3 in \1) {',
        text,
        flags=re.DOTALL
    )

    # 10. Fix `@Deprecated` with wrong syntax
    text = re.sub(r'@Deprecated\([^)]*replaceWith[^)]*\)', '', text)

    # 11. Fix SourceDebugExtension import
    text = text.replace('import kotlin.jvm.internal.SourceDebugExtension\n', '')
    text = text.replace('import kotlin.jvm.internal.SourceDebugExtension', '')

    if text != orig:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(text)
        return True
    return False

def main():
    files = list(BUILD_DIR.rglob('*.kt'))
    files = [f for f in files if '$' not in f.name]
    print(f"Found {len(files)} .kt files")
    
    count = 0
    for f in files:
        if fix_file(f):
            count += 1
    
    print(f"Fixed {count} files")

if __name__ == '__main__':
    main()
