import re, os
from collections import Counter

MAPPINGS_FILE = r"C:\Users\Ansgar\.gradle\caches\fabric-loom\1.21.11\net.fabricmc.yarn.1_21_11.1.21.11+build.1-v2\mappings.tiny"
SRC_DIR = r"C:\Users\Ansgar\Documents\Minecraft fabric injetion\jooon-reimagined-recomp\src\main\java"

method_map = {}
field_map = {}
class_import_map = {}
class_simple_map = {}

method_options = {}
field_options = {}

print("Parsing mappings file...")
with open(MAPPINGS_FILE, 'r', encoding='utf-8') as f:
    for line in f:
        line = line.rstrip('\n')
        parts = line.split('\t')
        if not parts:
            continue
        if parts[0] == 'c' and len(parts) >= 4:
            inter_path = parts[2]
            named_path = parts[3]
            dot_inter = inter_path.replace('/', '.')
            dot_named = named_path.replace('/', '.')
            class_import_map[dot_inter] = dot_named
            inter_simple = dot_inter.rsplit('.', 1)[-1]
            named_simple = dot_named.rsplit('.', 1)[-1]
            class_simple_map[inter_simple] = named_simple
        elif parts[0] == '' and len(parts) >= 6:
            kind = parts[1]
            if kind in ('m', 'f'):
                name_map = method_options if kind == 'm' else field_options
                inter = parts[4]
                named = parts[5]
                if inter not in name_map:
                    name_map[inter] = Counter()
                name_map[inter][named] += 1

for inter, options in method_options.items():
    method_map[inter] = options.most_common(1)[0][0]
for inter, options in field_options.items():
    field_map[inter] = options.most_common(1)[0][0]

print(f"  Classes: {len(class_simple_map)}, Methods: {len(method_map)}, Fields: {len(field_map)}")
print(f"  Method conflicts: {sum(1 for v in method_options.values() if len(v) > 1)}")
print(f"  Field conflicts: {sum(1 for v in field_options.values() if len(v) > 1)}")

# Compile regex patterns
# Method: .method_XXXX( or ::method_XXXX or .method_XXXX) etc.
meth_re = re.compile(r'\.(method_\d+)([\(\),;\s\n.])')
meth_ref_re = re.compile(r'::(method_\d+)')
# Field: .field_XXXX or (field_XXXX)
field_re = re.compile(r'\.(field_\d+)\b')
field_paren_re = re.compile(r'\(field_\d+\)')
# comp_XXXX (composite naming) - used as method calls or property access
comp_re = re.compile(r'\.(comp_\d+)([\(\),;\s\n.])')
comp_ref_re = re.compile(r'::(comp_\d+)')
# Class name positions (to be safe, use \b)
class_re = re.compile(r'\b(class_\d+)\b')
# imports
import_re = re.compile(r'import\s+(net\.minecraft(?:\.\w+)*\.class_\d+)\b')

files = []
for root, dirs, fnames in os.walk(SRC_DIR):
    for fn in fnames:
        if fn.endswith('.kt'):
            files.append(os.path.join(root, fn))

print(f"Processing {len(files)} files...")
fixed_count = 0

for filepath in files:
    with open(filepath, 'r', encoding='utf-8') as f:
        orig = f.read()
    content = orig

    # Fix imports
    def fix_import(m):
        imp = m.group(1)
        return f"import {class_import_map.get(imp, imp)}"
    content = import_re.sub(fix_import, content)

    # Fix methods
    def fix_method(m):
        meth = m.group(1)
        if meth in method_map:
            return f".{method_map[meth]}{m.group(2)}"
        return m.group(0)
    content = meth_re.sub(fix_method, content)

    def fix_method_ref(m):
        meth = m.group(1)
        if meth in method_map:
            return f"::{method_map[meth]}"
        return m.group(0)
    content = meth_ref_re.sub(fix_method_ref, content)

    # Fix fields
    def fix_field(m):
        fname = m.group(1)
        if fname in field_map:
            return f".{field_map[fname]}"
        return m.group(0)
    content = field_re.sub(fix_field, content)

    def fix_field_paren(m):
        fname = m.group(0)[1:-1]  # remove parens
        if fname in field_map:
            return f"({field_map[fname]})"
        return m.group(0)
    content = field_paren_re.sub(fix_field_paren, content)

    # Fix class names
    def fix_class(m):
        cname = m.group(1)
        if cname in class_simple_map:
            return class_simple_map[cname]
        return m.group(0)
    content = class_re.sub(fix_class, content)

    if content != orig:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        rp = os.path.relpath(filepath, SRC_DIR)
        print(f"  Mapped: {rp}")
        fixed_count += 1

print(f"\nDone! Mapped {fixed_count} files")
