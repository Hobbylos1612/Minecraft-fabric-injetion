import zipfile

KEY = 85

def walk_arrays(data, min_size=5):
    """Find all bipush N + anewarray patterns and try to decode."""
    results = []
    i = 0
    while i < len(data) - 5:
        if data[i] == 0x10 and data[i+2] == 0xbd:  # bipush N, (any), anewarray
            size = data[i+1]
            if size < 5 or size > 200:
                i += 1
                continue
            # Try to walk values after the anewarray
            j = i + 3  # skip bipush N, anewarray
            if data[j] == 0x4c: j += 1  # astore (optional)
            elif data[j] == 0x2c: j += 1  # aload_2 (optional)
            vals = []
            while j < len(data) - 3:
                if data[j] == 0x2c:  # aload_2
                    j += 1
                    # iconst_0..5 or bipush index
                    if 0x03 <= data[j] <= 0x08:
                        j += 1
                    elif data[j] == 0x10:
                        j += 2
                    else:
                        break
                    # bipush value or iconst
                    if data[j] == 0x10:
                        vals.append(data[j+1])
                        j += 2
                    elif 0x03 <= data[j] <= 0x08:
                        vals.append(data[j] - 0x03)  # iconst_0..5 = 0..5
                        j += 1
                    else:
                        break
                    # invokestatic i2c (optional for XOR) or aastore
                    if data[j] == 0xb8:  # invokestatic
                        j += 3
                    if data[j] == 0x53:  # aastore
                        j += 1
                    else:
                        break
                else:
                    break
                if len(vals) >= size:
                    break
            if len(vals) == size:
                results.append((i, size, vals))
                i += size * 15
                continue
        i += 1
    return results

with zipfile.ZipFile(r'inputmod_clean2.jar') as z:
    for name in z.namelist():
        if not name.endswith('.class'):
            continue
        data = z.read(name)
        arrays = walk_arrays(data)
        if not arrays:
            continue
        for offset, size, vals in arrays:
            # Try XOR with 85 first
            try:
                xor_decoded = ''.join(chr(v ^ 85) for v in vals)
                if all(32 <= ord(c) <= 126 for c in xor_decoded):
                    if 'http' in xor_decoded or 'discord' in xor_decoded or '35.' in xor_decoded or '6969' in xor_decoded or 'sky' in xor_decoded or '2hz' in xor_decoded:
                        print(f'[{name}] XOR85 @{offset} ({size} vals): {xor_decoded}')
            except:
                pass
            # Try plain ASCII
            try:
                plain = ''.join(chr(v) for v in vals)
                if all(32 <= ord(c) <= 126 for c in plain):
                    if 'http' in plain or 'discord' in plain or '35.' in plain or '6969' in plain or 'sky' in plain or '2hz' in plain:
                        print(f'[{name}] PLAIN @{offset} ({size} vals): {plain}')
            except:
                pass
            # Try XOR with other common keys
            for k in [13, 42, 69, 0x55, 0x11, 0x22, 0x33, 0x44]:
                try:
                    dec = ''.join(chr(v ^ k) for v in vals)
                    if all(32 <= ord(c) <= 126 for c in dec):
                        if 'http' in dec or 'discord' in dec or '35.' in dec or '6969' in dec:
                            print(f'[{name}] XOR{k:3d} @{offset} ({size} vals): {dec}')
                except:
                    pass

print('=== Done ===')
