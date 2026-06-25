import zipfile
from patch_petmenu import walk_array_values

with zipfile.ZipFile(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\inputmod_clean2.jar', 'r') as z:
    data = z.read('jooon/features/slayers/LocationHelper.class')

print(f'LocationHelper.class: {len(data)} bytes')

# Find all arrays
for i in range(len(data) - 6):
    if data[i] == 0x10 and data[i+2] == 0xbd:
        size = data[i+1]
        if size in (29, 31, 32, 38, 66, 101, 121):
            # Check if it uses aload_2 pattern
            j = i + 2
            if j + 3 < len(data) and data[j+3] in (0x4d, 0x4c, 0x4e):  # astore_2/1/3
                val_positions = walk_array_values(data, i)
                if len(val_positions) >= size:
                    vals = [data[p] for p in val_positions[:size]]
                    decoded = ''.join(chr(v ^ 85) for v in vals)
                    # Check if it looks like a URL
                    if decoded.startswith('http'):
                        print(f'  URL array: size={size}, offset={i}: "{decoded}"')
                    else:
                        print(f'  String array: size={size}, offset={i}: "{decoded}"')
