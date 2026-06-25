import zipfile, sys
sys.path.insert(0, r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP')
from patch_petmenu import walk_array_values

with zipfile.ZipFile(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\inputmod_clean2.jar', 'r') as z:
    data = z.read('jooon/features/other/PetMenu.class')

KEY = 85

patches = [
    (3004, 31, 'bBb'),
    (3642, 29, 'cCc'),
    (4262, 29, 'dDd'),
    (5794, 121, 'fFf'),
]

for offset, count, name in patches:
    if data[offset] != 0x10 or data[offset+1] != count:
        print(f'{name}: WRONG size, expected {count}, got {data[offset+1]}')
        continue
    
    val_positions = walk_array_values(data, offset)
    vals = [data[p] for p in val_positions[:count]]
    
    if name == 'fFf':
        decoded = ''.join(chr(v) for v in vals)
    else:
        decoded = ''.join(chr(v ^ KEY) for v in vals)
    
    print(f'{name} ({count} vals): "{decoded}"')
