import zipfile

jars = [r'inputmod.jar', r'inputmod_clean2.jar']
targets = [b'twopointfive', b'two', b'pointfive', b'2.5', b'ess', b'essfb', b'gje']

for jar in jars:
    print(f'=== {jar} ===')
    with zipfile.ZipFile(jar) as z:
        for name in z.namelist():
            if not (name.endswith('.class') or name.endswith('.json')):
                continue
            data = z.read(name)
            for t in targets:
                if t in data:
                    idx = data.find(t)
                    print(f'  PLAIN "{t.decode()}" in {name} @{idx}')
                # XOR with 85
                xor_t = bytes(b ^ 85 for b in t)
                if xor_t in data:
                    idx = data.find(xor_t)
                    print(f'  XOR85 "{t.decode()}" in {name} @{idx}')
print('=== Done ===')
