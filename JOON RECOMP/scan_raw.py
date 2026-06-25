import zipfile

with zipfile.ZipFile(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\inputmod_clean2.jar', 'r') as z:
    data = z.read('jooon/features/slayers/LocationHelper.class')

print(f'LocationHelper.class: {len(data)} bytes')
print('All arrays (bipush N + anewarray):')
for i in range(len(data) - 10):
    if data[i] == 0x10 and data[i+2] == 0xbd:
        size = data[i+1]
        j = i + 2
        after_arr = f'{data[j+3]:02x}' if j+3 < len(data) else 'EOF'
        print(f'  size={size:3d}, offset={i:5d}, after_anewarray=0x{after_arr}')
