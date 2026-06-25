#!/usr/bin/env python3
"""Remove embedded HTTP JARs and fix fabric.mod.json"""

import zipfile
import os
import shutil
import tempfile
import json

work = r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP'
jar_path = os.path.join(work, 'inputmod_clean2.jar')

# List of embedded JARs to remove
remove_jars = [
    'META-INF/jars/commons-logging-1.2.jar',
    'META-INF/jars/httpclient-4.5.13.jar',
    'META-INF/jars/httpcore-4.4.13.jar',
]

with zipfile.ZipFile(jar_path, 'r') as z:
    entries = {}
    for name in z.namelist():
        entries[name] = z.read(name)

print(f'Original entries: {len(entries)}')

# Remove the HTTP JARs
for jar in remove_jars:
    if jar in entries:
        del entries[jar]
        print(f'Removed: {jar}')
    else:
        print(f'Not found: {jar}')

# Fix fabric.mod.json - remove the "jars" section
if 'fabric.mod.json' in entries:
    f7 = json.loads(entries['fabric.mod.json'])
    if 'jars' in f7:
        print(f'Old jars section: {f7["jars"]}')
        del f7['jars']
        # Also ensure no "jars" is an empty list
        entries['fabric.mod.json'] = json.dumps(f7, indent=2).encode('utf-8')
        print('Removed jars section from fabric.mod.json')

# Write back
fd, tmp = tempfile.mkstemp(suffix='.jar')
os.close(fd)

with zipfile.ZipFile(tmp, 'w', zipfile.ZIP_DEFLATED) as zout:
    for name, data in sorted(entries.items()):
        zout.writestr(name, data)

shutil.move(tmp, jar_path)
print(f'\nFinal entries: {len(entries)}')
print(f'Updated: {jar_path}')
