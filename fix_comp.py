import re, os
from collections import Counter

MAPPINGS_FILE = r"C:\Users\Ansgar\.gradle\caches\fabric-loom\1.21.11\net.fabricmc.yarn.1_21_11.1.21.11+build.1-v2\mappings.tiny"
SRC_DIR = r"C:\Users\Ansgar\Documents\Minecraft fabric injetion\jooon-reimagined-recomp\src\main\java"

# Build comp_XXXX -> yarnName mapping
comp_map = {}
options = {}

print("Parsing comp_ mappings...")
with open(MAPPINGS_FILE, 'r', encoding='utf-8') as f:
    for line in f:
        parts = line.rstrip('\n').split('\t')
        if len(parts) >= 6 and parts[0] == '' and parts[1] in ('m', 'f'):
            inter = parts[4]
            named = parts[5]
            if inter.startswith('comp_'):
                if inter not in options:
                    options[inter] = Counter()
                options[inter][named] += 1

for inter, opts in options.items():
    comp_map[inter] = opts.most_common(1)[0][0]

print(f"  comp_ mappings: {len(comp_map)}")

# Process all kotlin files
files = []
for root, dirs, fnames in os.walk(SRC_DIR):
    for fn in fnames:
        if fn.endswith('.kt'):
            files.append(os.path.join(root, fn))

fixed_count = 0
# Sort by length descending for safety
items = sorted(comp_map.items(), key=lambda x: -len(x[0]))

for filepath in files:
    with open(filepath, 'r', encoding='utf-8') as f:
        orig = f.read()
    content = orig

    # Replace .comp_XXXX( with .yarnName(
    for inter, named in items:
        content = content.replace(f'.{inter}(', f'.{named}(')
        content = content.replace(f'::{inter}', f'::{named}')
        content = content.replace(f'.{inter})', f'.{named})')
        content = content.replace(f'.{inter},', f'.{named},')
        content = content.replace(f'.{inter} ', f'.{named} ')
        content = content.replace(f'.{inter}\n', f'.{named}\n')
        # Also handle property-style access
        content = content.replace(f'.{inter}.', f'.{named}.')
        content = content.replace(f'.{inter});', f'.{named});')

    if content != orig:
        with open(filepath, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"  Fixed: {os.path.relpath(filepath, SRC_DIR)}")
        fixed_count += 1

print(f"\nDone! Fixed {fixed_count} files")
