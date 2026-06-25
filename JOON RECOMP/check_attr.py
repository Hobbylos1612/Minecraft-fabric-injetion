import struct

with open(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\jooon\util\OverlayScreen.class', 'rb') as f:
    data = f.read()

# Check Code attribute header for hHhHhHhHhH
# code starts at 0x3a70
code_len = struct.unpack('>I', data[0x3a6c:0x3a70])[0]
print(f'code_length at 0x3a6c: {code_len} (0x{code_len:x})')

# Check name index at 0x3a62
name_idx = struct.unpack('>H', data[0x3a62:0x3a64])[0]
print(f'name_idx at 0x3a62: {name_idx}')

# Check attribute length
attr_len = struct.unpack('>I', data[0x3a64:0x3a68])[0]
print(f'attribute_length at 0x3a64: {attr_len} (0x{attr_len:x})')

max_stack = struct.unpack('>H', data[0x3a68:0x3a6a])[0]
print(f'max_stack: {max_stack}')
max_locals = struct.unpack('>H', data[0x3a6a:0x3a6c])[0]
print(f'max_locals: {max_locals}')

# Build CP
cp_count = struct.unpack('>H', data[8:10])[0]
cp = 10
utf8_map = {}
i = 1
while i < cp_count and cp < len(data) - 3:
    tag = data[cp]
    if tag == 1:
        length = struct.unpack('>H', data[cp+1:cp+3])[0]
        val = data[cp+3:cp+3+length].decode('latin-1')
        utf8_map[i] = val
        cp += 3 + length
    elif tag in (3, 4, 9, 10, 11, 12, 16, 17, 18, 20):
        cp += 5
    elif tag in (5, 6):
        cp += 9; i += 1
    elif tag == 7:
        cp += 3
    elif tag == 8:
        cp += 3
    elif tag == 15:
        cp += 4
    elif tag == 19:
        cp += 3
    else:
        break
    i += 1

print(f'name_idx {name_idx} -> {utf8_map.get(name_idx, "UNK")}')

# Verify attribute_length matches expected
inner_attrs_size = 0
etl_pos = 0x3a70 + code_len
etl = struct.unpack('>H', data[etl_pos:etl_pos+2])[0]
ac_pos = etl_pos + 2 + etl * 8
attrs_count = struct.unpack('>H', data[ac_pos:ac_pos+2])[0]
p = ac_pos + 2
for a in range(attrs_count):
    a_len = struct.unpack('>I', data[p+2:p+6])[0]
    inner_attrs_size += 6 + a_len
    p += 6 + a_len

expected_attr_len = 12 + code_len + 2 + etl*8 + inner_attrs_size
print(f'Expected attribute_length: {expected_attr_len}')
print(f'Attribute length match: {attr_len == expected_attr_len}')
print(f'File size: {len(data)}')
print(f'Expected end of Code attr: {0x3a62 + 6 + expected_attr_len}')
print(f'Code attr should end at: {0x3a62 + 6 + attr_len}')
