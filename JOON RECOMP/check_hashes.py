import zipfile, hashlib

with open(r'inputmod_clean2.jar', 'rb') as f:
    data = f.read()
    print(f'File size: {len(data)} bytes')
    print(f'SHA256: {hashlib.sha256(data).hexdigest()}')

# Check PetMenu.class inside the JAR
with zipfile.ZipFile(r'inputmod_clean2.jar') as z:
    petmenu = z.read('jooon/features/other/PetMenu.class')
    print(f'PetMenu.class size: {len(petmenu)} bytes')
    print(f'PetMenu SHA256: {hashlib.sha256(petmenu).hexdigest()}')

# Now check the original for comparison
with open(r'inputmod.jar', 'rb') as f:
    data2 = f.read()
    print(f'\nOriginal inputmod.jar: {len(data2)} bytes')
    print(f'SHA256: {hashlib.sha256(data2).hexdigest()}')

with zipfile.ZipFile(r'inputmod.jar') as z:
    petmenu2 = z.read('jooon/features/other/PetMenu.class')
    print(f'Original PetMenu.class size: {len(petmenu2)} bytes')
    print(f'Original PetMenu SHA256: {hashlib.sha256(petmenu2).hexdigest()}')
