"""More comprehensive decompiler fixer for Kotlin files."""
import re
import os
from pathlib import Path

BUILD_DIR = Path(r"C:\Users\Ansgar\Documents\Minecraft fabric injetion\jooon-reimagined-recomp\src\main\java\jooon")

def fix_missing_returns(text):
    """Add missing 'return' before the last expression in function bodies with non-Unit return type."""
    lines = text.split('\n')
    result = []
    i = 0
    in_body = 0
    body_start = -1
    func_return_type = None
    brace_count = 0
    while i < len(lines):
        line = lines[i]
        stripped = line.strip()
        
        # Detect function start: fun name(...): ReturnType {
        m = re.match(r'(\s*)(?:public|private|internal|protected|open|override|abstract)?\s*fun\s+\w+\([^)]*\)\s*(:\s*(\w+(?:<[^>]*>)?))?\s*\{', line)
        if m and not stripped.startswith('//') and not stripped.startswith('*'):
            indent = m.group(1)
            rt = m.group(3)
            if rt and rt not in ('Unit', 'Boolean', 'Int', 'Double', 'Float', 'Long', 'String', 'Void'):
                # Custom return type - might still return Unit via last expression
                pass
            if rt and rt != 'Unit':
                func_return_type = rt
            else:
                func_return_type = None
            in_body += 1
            result.append(line)
            i += 1
            continue
        
        # Track brace depth
        for c in stripped:
            if c == '{':
                brace_count += 1
            elif c == '}':
                brace_count -= 1
        
        # If we're at depth 0 (back to class/object level), reset
        if brace_count <= 0 and in_body > 0:
            # We left the function body
            in_body = 0
            func_return_type = None
        
        # If we're at the last line before closing brace of a function
        if func_return_type and brace_count == 1:
            next_line = lines[i+1] if i+1 < len(lines) else ''
            if next_line.strip() == '}' and stripped and not stripped.startswith('return') and not stripped.startswith('if') and not stripped.startswith('for') and not stripped.startswith('while') and not stripped.startswith('//') and not stripped.startswith('*'):
                # Check if the line is an expression (not a control flow statement)
                if not re.match(r'^\s*(var|val|if|for|while|when|try|throw|return|class|object|fun|private|public|internal|protected|import|package)', stripped):
                    # Add return
                    indent = re.match(r'^(\s*)', line).group(1)
                    result.append(f'{indent}return {stripped}')
                    i += 1
                    continue
        
        result.append(line)
        i += 1
    
    return '\n'.join(result)

def fix_data_class(text):
    """Fix data class constructors to have val/var params."""
    lines = text.split('\n')
    result = []
    i = 0
    while i < len(lines):
        line = lines[i]
        # Match data class declaration (possibly multi-line)
        m = re.match(r'^(\s*)data\s+class\s+(\w+)\s*\(', line)
        if m:
            indent = m.group(1)
            class_name = m.group(2)
            # Collect all lines until the closing )
            params_lines = [line[m.end():]]
            while ')' not in line and i+1 < len(lines):
                i += 1
                line = lines[i]
                params_lines.append(line)
            # Extract the closing part after )
            close_idx = line.index(')') if ')' in line else len(line)
            after_paren = line[close_idx+1:]
            params_lines[-1] = line[:close_idx]
            
            params_text = '\n'.join(params_lines)
            # Parse parameters
            # Remove outer parens content and split by commas
            params_text = params_text.strip()
            if params_text.startswith('('):
                params_text = params_text[1:]
            if params_text.endswith(')'):
                params_text = params_text[:-1]
            
            # Split by top-level commas (not inside nested parens)
            params = split_params(params_text)
            fixed_params = []
            for p in params:
                p = p.strip()
                if p and not re.match(r'^(val|var|override|abstract|open|private|public|internal|protected)\b', p):
                    p = 'val ' + p
                fixed_params.append(p)
            
            if fixed_params != params:
                # Reconstruct
                new_line = f'{indent}data class {class_name}({", ".join(fixed_params)}){after_paren}'
                result.append(new_line)
            else:
                result.append(lines[i])  # not changed
        else:
            result.append(line)
        i += 1
    return '\n'.join(result)

def split_params(text):
    """Split constructor params by top-level commas."""
    result = []
    depth = 0
    current = []
    for c in text:
        if c in ('(', '<', '['):
            depth += 1
            current.append(c)
        elif c in (')', '>', ']'):
            depth -= 1
            current.append(c)
        elif c == ',' and depth == 0:
            result.append(''.join(current).strip())
            current = []
        else:
            current.append(c)
    if current:
        result.append(''.join(current).strip())
    return result

def fix_hashcode(text):
    """Fix Too many arguments for fun hashCode() by removing params."""
    lines = text.split('\n')
    result = []
    i = 0
    while i < len(lines):
        line = lines[i]
        m = re.match(r'^(\s*)fun\s+hashCode\([^)]*\)(\s*:\s*Int)?\s*\{', line)
        if m:
            indent = m.group(1)
            # Check if there are actual params (not empty)
            params = m.group(0)
            if '(' in params and ')' in params:
                inner = params[params.index('(')+1:params.index(')')]
                if inner.strip():
                    line = f'{indent}fun hashCode(): Int {{'
        result.append(line)
        i += 1
    return '\n'.join(result)

def fix_iteration(text):
    """Fix Java-style iteration to Kotlin for-loops."""
    # Simple pattern: val iter = list.iterator() while(iter.hasNext()) { val item = iter.next() }
    text = re.sub(
        r'val\s+(\w+)\s*:\s*[^=]*=\s*(\w+)\.iterator\(\)\s*'  # val iter = list.iterator()
        r'\n\s*while\s*\(\s*\1\.hasNext\(\)\s*\)\s*\{?\s*'     # while(iter.hasNext()) {
        r'\n\s*val\s+(\w+)\s*=\s*\1\.next\(\)\s*(?:as\s+(\w+))?',  # val item = iter.next() as Type
        r'for (\3 in \2) {',
        text
    )
    
    # Simpler pattern: for(element in list) without explicit iterator
    # Replace `val it: java.util.Iterator = list.iterator()` with implicit
    text = re.sub(
        r'val\s+\w+\s*:\s*(?:java\.util\.)?Iterator[^=]*=\s*(\w+)\.iterator\(\)\s*\n\s*while\s*\(\s*\w+\.hasNext\(\)\s*\)\s*\{',
        r'for (item in \1) {',
        text
    )
    
    return text

def fix_property_init(text):
    """Fix uninitialized properties by adding default values where possible."""
    # This is hard to do perfectly. We'll handle common cases.
    # Match: private final var name: Type (no = ...) with newlines before get/set
    
    # For Boolean properties with custom getter
    text = re.sub(
        r'(@JvmField)?\s*(public|private|internal)?\s*(final)?\s*(var|val)\s+(\w+)\s*:\s*(Boolean|Int|Double|Float|Long|String)\s*\n\s*(public|private|internal)?\s*(final)?\s*(get)\b',
        lambda m: f'{m.group(1) or ""} {m.group(2) or ""} {m.group(3) or ""} {m.group(4) or ""} {m.group(5)}: {m.group(6)} = false\n{m.group(7) or ""} {m.group(8) or ""} {m.group(9)}' if m.group(6) == 'Boolean' 
                  else f'{m.group(1) or ""} {m.group(2) or ""} {m.group(3) or ""} {m.group(4) or ""} {m.group(5)}: {m.group(6)} = 0\n{m.group(7) or ""} {m.group(8) or ""} {m.group(9)}',
        text
    )
    
    return text

def fix_type_args(text):
    """Fix missing type arguments on generic interfaces."""
    text = re.sub(r'One type argument expected for interface \'Iterable\'.', '', text)
    # Fix raw usage: val x: Iterable -> val x: Iterable<*>
    text = re.sub(r'(?<!\w):\s*Iterable\b(?!\s*[<*])', ': Iterable<*>', text)
    text = re.sub(r'(?<!\w):\s*Iterator\b(?!\s*[<*])', ': Iterator<*>', text)
    text = re.sub(r'(?<!\w):\s*List\b(?!\s*[<*])', ': List<*>', text)
    text = re.sub(r'(?<!\w):\s*Collection\b(?!\s*[<*])', ': Collection<*>', text)
    text = re.sub(r'(?<!\w):\s*Set\b(?!\s*[<*])', ': Set<*>', text)
    text = re.sub(r'(?<!\w):\s*Map\b(?!\s*[<*])', ': Map<*, *>', text)
    return text

def fix_val_reassign(text):
    """Fix val being reassigned to var."""
    # Find patterns where a val is declared and later assigned
    # Simple: val name = ... followed by name = ... later
    lines = text.split('\n')
    result = []
    vals = {}  # name -> line index
    i = 0
    while i < len(lines):
        line = lines[i]
        stripped = line.strip()
        # Track val declarations
        m = re.match(r'^\s*(public|private|internal|protected)?\s*val\s+(\w+)\s*[=:]', stripped)
        if m:
            vals[m.group(2)] = i
        result.append(line)
        i += 1
    return '\n'.join(result)

def fix_containsit(text):
    """Fix 'containsit' decompiler artifacts."""
    text = text.replace('containsit', 'this')
    text = text.replace('joinToStringit', 'this')
    text = text.replace('getFirst', 'first')
    text = text.replace('getLast', 'last')
    return text

def main():
    files = list(BUILD_DIR.rglob('*.kt'))
    files = [f for f in files if '$' not in f.name]
    print(f"Found {len(files)} .kt files")
    
    counts = {'missing_return': 0, 'data_class': 0, 'hashcode': 0, 'type_args': 0,
              'containsit': 0, 'iteration': 0, 'property_init': 0}
    
    for f in files:
        text = f.read_text('utf-8', errors='replace')
        orig = text
        
        text = fix_iteration(text)
        text = fix_type_args(text)
        text = fix_containsit(text)
        text = fix_hashcode(text)
        text = fix_data_class(text)
        text = fix_property_init(text)
        text = fix_missing_returns(text)
        
        if text != orig:
            f.write_text(text, 'utf-8')
    
    print("All files processed")

if __name__ == '__main__':
    main()
