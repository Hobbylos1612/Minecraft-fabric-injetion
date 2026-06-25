import zipfile, json

with zipfile.ZipFile(r'inputmod.jar') as z:
    names = z.namelist()
    print('=== Original inputmod.jar ===')
    for n in names:
        if 'META-INF' in n or n.endswith('.jar'):
            info = z.getinfo(n)
            print(f'  {n:55s} {info.file_size:>8d}B')
    fmj = json.loads(z.read('fabric.mod.json'))
    print(f'\njars: {fmj.get("jars", [])}')
    print(f'depends: {fmj.get("depends", {})}')

with zipfile.ZipFile(r'inputmod_clean2.jar') as z:
    names = z.namelist()
    print('\n=== Patched inputmod_clean2.jar ===')
    for n in names:
        if 'META-INF' in n or n.endswith('.jar'):
            info = z.getinfo(n)
            print(f'  {n:55s} {info.file_size:>8d}B')
    fmj = json.loads(z.read('fabric.mod.json'))
    print(f'\njars: {fmj.get("jars", [])}')
    print(f'depends: {fmj.get("depends", {})}')
