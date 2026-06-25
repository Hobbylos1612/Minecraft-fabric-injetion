import zipfile, sys
sys.path.insert(0, r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP')
from patch_petmenu import walk_array_values

KEY = 85

with zipfile.ZipFile(r'inputmod_clean2.jar') as z:
    for name in sorted(z.namelist()):
        if not name.endswith('.class'):
            continue
        data = z.read(name)
        i = 0
        while i < len(data) - 5:
            if data[i] == 0x10 and data[i+2] == 0xbd:  # bipush N, ?, anewarray
                size = data[i+1]
                if 5 <= size <= 200:
                    vals_pos = walk_array_values(data, i)
                    if len(vals_pos) >= 5:
                        vals = [data[p] for p in vals_pos]
                        # Try XOR with key 85
                        try:
                            s = ''.join(chr(v ^ 85) for v in vals)
                            if all(32 <= ord(c) <= 126 for c in s):
                                if 'http' in s or 'discord' in s or '35.' in s or '6969' in s or 'sky' in s or '2hz' in s or 'api/webhook' in s:
                                    print(f'XOR85 [{name}] @{i} ({size} vals): {s}')
                        except:
                            pass
                        # Try plain text
                        try:
                            s = ''.join(chr(v) for v in vals)
                            if all(32 <= ord(c) <= 126 for c in s):
                                if 'http' in s or 'discord' in s or '35.' in s or '6969' in s:
                                    print(f'PLAIN [{name}] @{i} ({size} vals): {s}')
                        except:
                            pass
                i += 3
            else:
                i += 1

print('=== Done ===')
