with open(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\jooon\util\OverlayScreen.class', 'rb') as f:
    d = f.read()

print('=== gGgGgGgGgG @0x38ee ===')
print(f'First 60 bytes: {d[0x38ee:0x392a].hex()}')
print(f'bipush at 0x38ee: {hex(d[0x38ee])} {d[0x38ee+1]} (num elements)')

print()
print('=== hHhHhHhHhH @0x3a70 ===')
print(f'First 40 bytes: {d[0x3a70:0x3a98].hex()}')
print(f'iconst: {d[0x3a70]} -> {d[0x3a70]} elements')
for i in range(3):
    val_pos = 0x3a70 + 8 + i*8
    val = d[val_pos]
    ch = chr(val) if 32 <= val < 127 else '?'
    print(f'  Element {i}: {val} ({ch})')

print()
print('=== iIiIiIiIiI @0x3bd0 ===')
print(f'First 50 bytes: {d[0x3bd0:0x3c02].hex()}')
print(f'iconst: {d[0x3bd0]} -> {d[0x3bd0]} elements')
for i in range(4):
    val_pos = 0x3bd0 + 8 + i*8
    val = d[val_pos]
    ch = chr(val) if 32 <= val < 127 else '?'
    print(f'  Element {i}: {val} ({ch})')
