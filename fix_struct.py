import re
import os
import glob

BUILD_DIR = r"C:\Users\Ansgar\Documents\Minecraft fabric injetion\jooon-reimagined-recomp\src\main\java\jooon"

def find_matching_paren(s, start):
    """Find matching closing paren from start (which should be at '(')."""
    depth = 0
    for i in range(start, len(s)):
        if s[i] == '(':
            depth += 1
        elif s[i] == ')':
            depth -= 1
            if depth == 0:
                return i
    return -1

def find_matching_angle(s, start):
    """Find matching closing angle bracket from start."""
    depth = 0
    for i in range(start, len(s)):
        if s[i] == '<':
            depth += 1
        elif s[i] == '>':
            depth -= 1
            if depth == 0:
                return i
    return -1

def consume_expression(s, start):
    """
    Given string s and index start pointing to start of an expression,
    return the index AFTER the expression ends.
    """
    if start >= len(s):
        return start

    # Handle parenthesized expression
    if s[start] == '(':
        end = find_matching_paren(s, start)
        if end >= 0:
            return end + 1
        return start + 1

    # Handle number literal
    m = re.match(r'[-+]?\d+\.?\d*(?:[fFdD]|\.\d+)?', s[start:])
    if m and m.end() > 0:
        return start + m.end()

    # Handle backtick-quoted identifiers: `variable_name`
    if s[start] == '`':
        end = s.find('`', start + 1)
        if end >= 0:
            return end + 1
        return start + 1

    # Handle identifier-based expression: word chars, dots, generics, calls
    i = start
    if i < len(s) and (s[i].isalpha() or s[i] == '_' or s[i] == '@'):
        while i < len(s):
            # Consume identifier
            m = re.match(r'[a-zA-Z_$][a-zA-Z0-9_$]*', s[i:])
            if not m:
                break
            i += m.end()

            # Consume generics <...>
            while i < len(s) and s[i] == '<':
                end = find_matching_angle(s, i)
                if end >= 0:
                    i = end + 1
                else:
                    break

            # Consume call arguments (...)
            while i < len(s) and s[i] == '(':
                end = find_matching_paren(s, i)
                if end >= 0:
                    i = end + 1
                else:
                    i += 1
                    break

            # Consume chained .field or .method()
            if i < len(s) and s[i] == '.' and i + 1 < len(s) and (s[i+1].isalpha() or s[i+1] == '_'):
                i += 1
                continue

            # Consume ?. safe call
            if i < len(s) and s[i] == '?' and i + 1 < len(s) and s[i+1] == '.':
                i += 2
                continue
            # Consume !! non-null assertion
            if i < len(s) and s[i] == '!' and i + 1 < len(s) and s[i+1] == '!':
                i += 2
                continue

            break

        # Do NOT consume operators after the expression.
        # The expression is just the first identifier/member/call.
        return i

    return start

def find_nth_arg(s, start, n):
    """
    Find the n-th argument in a function call starting at `start` (which is after '(').
    Returns (arg_start, arg_end) where arg_end is the index after the argument.
    n is 1-indexed.
    """
    i = start
    current_arg = 1
    while i < len(s) and current_arg <= n:
        if s[i] == ' ' or s[i] == '\t':
            i += 1
            continue
        arg_start = i
        arg_end = consume_expression(s, i)
        if arg_end == arg_start:
            # No expression consumed; skip char
            i += 1
            continue
        if current_arg == n:
            return (arg_start, arg_end)
        # Skip comma
        i = arg_end
        while i < len(s) and (s[i] == ' ' or s[i] == '\t'):
            i += 1
        if i < len(s) and s[i] == ',':
            i += 1
        current_arg += 1
    return (-1, -1)


def fix_c_cast(s, cast_type, conversion_method):
    """
    Replace `(cast_type)expr` with `expr.conversion_method()`.
    Returns modified string.
    """
    pattern = f'({cast_type})'
    result = list(s)
    i = 0
    while i < len(result):
        # Check for (cast_type) at this position
        match = re.match(r'\(\s*' + cast_type + r'\s*\)', ''.join(result[i:i+20]))
        if match:
            cast_start = i
            cast_end = i + match.end()

            # Skip whitespace after the cast
            j = cast_end
            while j < len(result) and result[j] in ' \t':
                j += 1

            # Check the character after the cast
            if j < len(result):
                # Try to consume any expression after the cast
                expr_start = j
                expr_end = consume_expression(''.join(result), j)

                if expr_end > expr_start:
                    expr_str = ''.join(result[expr_start:expr_end])
                    replacement = f'{expr_str}.{conversion_method}()'
                    new_part = list(replacement)
                    result[cast_start:expr_end] = new_part
                    i = cast_start + len(new_part)
                    continue

            i = cast_end
            continue

        i += 1

    return ''.join(result)


def fix_coerce_calls(s):
    """
    Transform `func(A, B, C)` into `(A).func(B, C)` for coerceIn/coerceAtLeast/coerceAtMost.
    After RangesKt. prefix is removed.
    Process right-to-left to handle nested calls correctly.
    """
    result = list(s)
    # Map of function names to number of arguments (excluding receiver)
    # Format: func(A, B, C) → (A).func(B, C)  (to → Pair)
    funcs = {
        'coerceIn': (3, None),
        'coerceAtLeast': (2, None),
        'coerceAtMost': (2, None),
        'roundToInt': (1, None),
        'roundToLong': (1, None),
        'to': (2, 'Pair'),  # to(A, B) → Pair(A, B)
    }
    i = len(result) - 1
    while i >= 0:
        for func_name, (num_args, rename) in funcs.items():
            fn_len = len(func_name)
            if (i + fn_len <= len(result) and
                ''.join(result[i:i+fn_len]) == func_name and
                i + fn_len < len(result) and result[i+fn_len] == '('):
                # Check previous char is not alphanumeric or dot (avoid obj.func)
                if i > 0 and (result[i-1].isalnum() or result[i-1] == '_' or result[i-1] == '.'):
                    break
                paren_start = i + fn_len
                paren_end = find_matching_paren(''.join(result), paren_start)
                if paren_end < 0:
                    break

                content = ''.join(result[paren_start+1:paren_end])
                args = split_args(content)

                if len(args) >= num_args:
                    new_name = rename if rename else func_name
                    if rename:
                        # to(A, B) → Pair(A, B)
                        args_str = ', '.join(a.strip() for a in args[:num_args])
                        replacement = f'{new_name}({args_str})'
                    else:
                        # func(A, B, C) → (A).func(B, C)
                        arg1 = args[0].strip()
                        rest_args = ', '.join(a.strip() for a in args[1:num_args])
                        replacement = f'({arg1}).{new_name}({rest_args})'
                    new_part = list(replacement)
                    result[i:paren_end+1] = new_part
                    break
        i -= 1

    return ''.join(result)


def split_args(s):
    """Split function call arguments by comma, respecting parentheses nesting."""
    args = []
    depth = 0
    current = []
    i = 0
    while i < len(s):
        ch = s[i]
        if ch == '(':
            depth += 1
            current.append(ch)
        elif ch == ')':
            depth -= 1
            current.append(ch)
        elif ch == ',' and depth == 0:
            args.append(''.join(current).strip())
            current = []
        else:
            current.append(ch)
        i += 1
    if current:
        args.append(''.join(current).strip())
    return args


def remove_source_debug(s):
    """Remove @SourceDebugExtension annotations and imports."""
    s = re.sub(r'^import kotlin\.jvm\.internal\.SourceDebugExtension\s*\n', '', s, flags=re.MULTILINE)
    s = re.sub(r'^@SourceDebugExtension\(\["SMAP.*?^\)\]\)\s*\n', '', s, flags=re.MULTILINE | re.DOTALL)
    s = re.sub(r'^@SourceDebugExtension\(.*?\)\s*\n', '', s, flags=re.MULTILINE)
    return s


def process_file(filepath):
    with open(filepath, 'r', encoding='utf-8') as f:
        content = f.read()

    original = content

    # 1. Remove @SourceDebugExtension
    content = remove_source_debug(content)

    # 2. Fix java.lang.* prefixes
    content = re.sub(r'(?<![.\w])java\.lang\.(Double|Float|Integer|Boolean|Long|String|Math|Void|Short|Byte|Character)(?![.\w])', r'\1', content)

    # 3. Remove Kotlin stdlib class prefixes (just the class name followed by dot)
    for cls in ['CollectionsKt', 'RangesKt', 'SequencesKt', 'MathKt', 'StringsKt',
                'MapsKt', 'ArraysKt', 'TuplesKt', 'LazyKt', 'IntrinsicsKt', 'Intrinsics',
                'JvmPrimitiveKt', 'ExceptionsKt', 'KotlinVersion', 'ReflectionKt']:
        content = re.sub(r'(?<![a-zA-Z_])' + cls + r'\.', '', content)

    # 4. TuplesKt.to( -> Pair(
    content = re.sub(r'TuplesKt\.to\(', 'Pair(', content)

    # 5. INSTANCE. prefix (preceded by . or non-letter)
    content = re.sub(r'(?<!\w)INSTANCE\.', '', content)

    # 6. size() on collections -> size
    content = re.sub(r'(?<=[a-zA-Z])size\(\)', 'size', content)

    # 7. Fix common Yarn obfuscated names (preceded by . or non-letter)
    content = re.sub(r'(?<!\w)field_1352(?!\w)', 'x', content)
    content = re.sub(r'(?<!\w)field_1351(?!\w)', 'y', content)
    content = re.sub(r'(?<!\w)field_1350(?!\w)', 'z', content)
    content = re.sub(r'(?<!\w)field_1353(?!\w)', 'z', content)
    content = re.sub(r'(?<!\w)method_1551\(\)', 'getInstance()', content)
    content = re.sub(r'(?<!\w)field_1724(?!\w)', 'player', content)

    # 8. Fix Unit.INSTANCE -> Unit
    content = re.sub(r'Unit\.INSTANCE', 'Unit', content)

    # 9. Remove /* $VF was: ... */ comments
    content = re.sub(r'/\\* \$VF was: [^*]+\*/', '', content)

    # 10. Remove (ConfigEntryNode)Result.constructor_impl/ constructor_impl  casts
    content = re.sub(r'\(\w+\)Result\.constructor_impl/\* \$VF was: constructor-impl \*/\(', 'Result(', content)

    # 11. Fix C-casts (int)expr -> expr.toInt(), etc.
    casts = [
        (r'double', 'toDouble'),
        (r'float', 'toFloat'),
        (r'int', 'toInt'),
        (r'long', 'toLong'),
        (r'boolean', 'toBoolean'),
        (r'byte', 'toByte'),
        (r'short', 'toShort'),
        (r'char', 'toChar'),
    ]
    for cast_type, method in casts:
        content = fix_c_cast(content, cast_type, method)

    # 9. Fix coerceIn/coerceAtLeast/coerceAtMost calls
    content = fix_coerce_calls(content)

    if content != original:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        return True
    return False


def main():
    files = glob.glob(os.path.join(BUILD_DIR, '**', '*.kt'), recursive=True)
    fixed = 0
    for f in sorted(files):
        if process_file(f):
            fixed += 1
            print(f"Fixed: {os.path.basename(f)}")
    print(f"\nFixed {fixed} files")

if __name__ == '__main__':
    main()
