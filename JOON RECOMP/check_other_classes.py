import zipfile

with zipfile.ZipFile(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\inputmod_clean2.jar', 'r') as z:
    for name in z.namelist():
        if not name.endswith('.class'):
            continue
        data = z.read(name)
        has_array = False
        for i in range(len(data) - 6):
            if data[i] == 0x10 and data[i+2] == 0xbd:
                size = data[i+1]
                if size in (29, 31, 32, 38, 66, 79, 101, 121):
                    if not has_array:
                        print(f'{name}: arrays of size {size}', end='')
                        has_array = True
                    else:
                        print(f', {size}', end='')
        if has_array:
            print()
