"""SAFE fixes - no structural changes, just simple substitutions."""
import re, os
from pathlib import Path

BUILD_DIR = Path(r"C:\Users\Ansgar\Documents\Minecraft fabric injetion\jooon-reimagined-recomp\src\main\java\jooon")

def fix_file(path):
    with open(path, 'r', encoding='utf-8', errors='replace') as f:
        text = f.read()
    orig = text
    
    # 1. Java casts: (double)value -> value.toDouble(), etc.
    text = re.sub(r'\(double\)\s*\(([^)]+)\)', r'(\1).toDouble()', text)
    text = re.sub(r'\(float\)\s*\(([^)]+)\)', r'(\1).toFloat()', text)
    text = re.sub(r'\(int\)\s*\(([^)]+)\)', r'(\1).toInt()', text)
    text = re.sub(r'\(long\)\s*\(([^)]+)\)', r'(\1).toLong()', text)
    text = re.sub(r'\(boolean\)\s*\(([^)]+)\)', r'(\1).toBoolean()', text)
    text = re.sub(r'\(double\)\s+(\w+(?:\.\w+)*)', r'(\1).toDouble()', text)
    text = re.sub(r'\(float\)\s+(\w+(?:\.\w+)*)', r'(\1).toFloat()', text)
    text = re.sub(r'\(int\)\s+(\w+(?:\.\w+)*)', r'(\1).toInt()', text)
    text = re.sub(r'\(long\)\s+(\w+(?:\.\w+)*)', r'(\1).toLong()', text)
    text = re.sub(r'\(boolean\)\s+(\w+(?:\.\w+)*)', r'(\1).toBoolean()', text)
    
    # 2. Word.INSTANCE -> Word (only when INSTANCE follows a word)
    text = re.sub(r'(?<!\w)(\w+)\.INSTANCE\b', r'\1', text)
    
    # 3. Standalone INSTANCE (not preceded by word) 
    text = re.sub(r'(?<!\w)INSTANCE\.', r'', text)
    
    # 4. collectionSizeOrDefault -> coerceAtLeast
    text = re.sub(r'collectionSizeOrDefault\(([^,]+),\s*(\d+)\)', r'(\1).coerceAtLeast(\2)', text)
    
    # 5. SourceDebugExtension import
    text = text.replace('import kotlin.jvm.internal.SourceDebugExtension\n', '')
    text = text.replace('import kotlin.jvm.internal.SourceDebugExtension', '')
    text = re.sub(r'@SourceDebugExtension\([^)]*\)\s*', '', text)
    
    # 6. Remove final on top-level properties (private|public|internal) final val/var
    text = re.sub(r'^(private|public|internal)\s+final\s+(val|var)\s', r'\1 \2 ', text, flags=re.MULTILINE)
    
    # 7. Remove @JvmField annotation
    text = re.sub(r'@JvmField\s*\n\s*', '', text)
    text = re.sub(r'@kotlin\.jvm\.JvmField\s*\n\s*', '', text)
    
    # 8. Fix java.util.Iterator, Collection, etc. - add * type arg
    text = re.sub(r'(?<!\w):\s*(java\.util\.)?Iterable\b(?!\s*[<*])', ': \g<1>Iterable<*>', text)
    text = re.sub(r'(?<!\w):\s*(java\.util\.)?Iterator\b(?!\s*[<*])', ': \g<1>Iterator<*>', text)
    text = re.sub(r'(?<!\w):\s*(java\.util\.)?Collection\b(?!\s*[<*])', ': \g<1>Collection<*>', text)
    text = re.sub(r'(?<!\w):\s*(java\.util\.)?List\b(?!\s*[<*])', ': \g<1>List<*>', text)
    text = re.sub(r'(?<!\w):\s*(java\.util\.)?Set\b(?!\s*[<*])', ': \g<1>Set<*>', text)
    text = re.sub(r'(?<!\w):\s*(java\.util\.)?Map\b(?!\s*[<*])', ': \g<1>Map<*, *>', text)
    
    # 9. Fix hashCode(params) -> hashCode()
    text = re.sub(r'fun hashCode\(([^)]+)\)(\s*:\s*Int)?\s*\{', r'fun hashCode()\2 {', text)
    
    # 10. Fix primitive type references
    text = re.sub(r'(?<!\w):\s*double\b(?!\w)', ': Double', text)
    text = re.sub(r'(?<!\w):\s*float\b(?!\w)', ': Float', text)
    text = re.sub(r'(?<!\w):\s*int\b(?!\w)', ': Int', text)
    text = re.sub(r'(?<!\w):\s*boolean\b(?!\w)', ': Boolean', text)
    text = re.sub(r'(?<!\w):\s*long\b(?!\w)', ': Long', text)
    text = re.sub(r'(?<!\w):\s*char\b(?!\w)', ': Char', text)
    
    # 11. Fix Double.compare, Integer.compare etc.
    text = re.sub(r'Double\.compare\(([^,]+),\s*([^)]+)\)', r'(\1).compareTo(\2)', text)
    text = re.sub(r'Integer\.compare\(([^,]+),\s*([^)]+)\)', r'(\1).compareTo(\2)', text)
    text = re.sub(r'Float\.compare\(([^,]+),\s*([^)]+)\)', r'(\1).compareTo(\2)', text)
    text = re.sub(r'Boolean\.compare\(([^,]+),\s*([^)]+)\)', r'(\1).compareTo(\2)', text)
    text = re.sub(r'Long\.compare\(([^,]+),\s*([^)]+)\)', r'(\1).compareTo(\2)', text)
    
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
