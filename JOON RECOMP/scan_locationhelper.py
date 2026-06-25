import zipfile

with zipfile.ZipFile(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\inputmod_clean2.jar', 'r') as z:
    data = z.read('jooon/features/slayers/LocationHelper.class')

print(f'LocationHelper.class: {len(data)} bytes')

# Find all arrays
for i in range(len(data) - 10):
    if data[i] == 0x10 and data[i+2] == 0xbd:
        size = data[i+1]
        j = i + 2
        if data[j] == 0xbd:
            j += 3  # skip anewarray
            store_type = None
            if data[j] == 0x4d:  # astore_2
                store_type = 2
                j += 1
            elif data[j] == 0x4c:  # astore_1
                store_type = 1
                j += 1
            elif data[j] == 0x4e:  # astore_3
                store_type = 3
                j += 1
            elif data[j] == 0x4b:  # astore_0
                store_type = 0
                j += 1
            else:
                store_type = f'unknown({data[j]:02x})'
            
            if isinstance(store_type, int) and store_type in (0, 1, 2, 3):
                aload_op = 0x2a + store_type  # aload_N
                
                # Walk values
                j_start = j
                vals = []
                actual_count = 0
                while j < len(data) - 3:
                    if data[j] == aload_op:
                        j += 1
                        if data[j] in range(0x03, 0x09):
                            j += 1
                        elif data[j] == 0x10:
                            j += 2
                        else:
                            break
                        if data[j] == 0x10:
                            vals.append(data[j+1])
                            actual_count += 1
                            j += 2
                        else:
                            break
                        if data[j] == 0xb8:
                            j += 3
                        else:
                            break
                        if data[j] == 0x53:
                            j += 1
                        else:
                            break
                    else:
                        break
                
                if actual_count >= size:
                    first_vals = vals[:size]
                    decoded_xor = ''.join(chr(v ^ 85) for v in first_vals)
                    decoded_ascii = ''.join(chr(v) for v in first_vals)
                    print(f'Array size={size} at offset {i}, store_type={store_type}:')
                    print(f'  XOR(85): "{decoded_xor}"')
                    if decoded_xor != decoded_ascii:
                        print(f'  ASCII:   "{decoded_ascii}"')
                    print()
