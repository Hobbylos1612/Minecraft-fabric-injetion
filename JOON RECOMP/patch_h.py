import struct

with open(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\jooon\util\OverlayScreen.class', 'rb') as f:
    data = bytearray(f.read())

# First patch gGgGgGgGgG: bipush 7 (10 07 at 0x38ee) -> iconst_3 (06) + NOP (00)
data[0x38ee] = 0x06
data[0x38ef] = 0x00
for i in range(0x390c, 0x390c + 32):
    data[i] = 0x00
print('gGgGgGgGgG: sky (115,107,121)')

# Patch iIiIiIiIiI: iconst_4->iconst_3, values 109->101, 111->117, NOP element 3
data[0x3bd0] = 0x06
data[0x3be0] = 0x65
data[0x3be8] = 0x75
for i in range(0x3bed, 0x3bf5):
    data[i] = 0x00
print('iIiIiIiIiI: .eu (46,101,117)')

# Now patch hHhHhHhHhH: 46,50,104,122 (.2hz)
sig = bytes([0x06, 0xBD, 0x01, 0x27, 0x4C])
idx = data.find(sig)
if idx != -1:
    print(f'hHhHhHhHhH bytecode at file offset {hex(idx)}')
    
    if abs(idx - 0x3a70) < 10:
        print('Confirmed: method location matches')
    
    code_length_field = idx - 4
    code_length = struct.unpack('>I', data[code_length_field:code_length_field+4])[0]
    print(f'Current code_length: {code_length}')
    
    # Patch iconst_3 -> iconst_4 (06 -> 07)
    data[idx] = 0x07
    print('Changed iconst_3 to iconst_4')
    
    # Element 0: value 105 -> 46 at idx+8
    data[idx+8] = 0x2E
    print('Element 0: 105 -> 46')
    
    # Element 1: value 121 -> 50 at idx+16
    data[idx+16] = 0x32
    print('Element 1: 121 -> 50')
    
    # Element 2: value 117 -> 104 at idx+24
    data[idx+21] = 0x2B
    data[idx+22] = 0x05
    data[idx+23] = 0x10
    data[idx+24] = 104
    data[idx+25] = 0xB8
    data[idx+26] = 0x01
    data[idx+27] = 0x2B
    data[idx+28] = 0x53
    print('Element 2: 117 -> 104')
    
    # Element 3: insert new element (8 bytes) at idx+29
    element3 = bytes([
        0x2B, 0x06,
        0x10, 0x7A,
        0xB8, 0x01, 0x2B,
        0x53
    ])
    
    insert_pos = idx + 29
    data[insert_pos:insert_pos] = element3
    
    new_code_length = code_length + 8
    data[code_length_field:code_length_field+4] = struct.pack('>I', new_code_length)
    print(f'Updated code_length: {code_length} -> {new_code_length}')
    
    # Now find and update LineNumberTable
    # After code[] and exception_table, there are attributes
    # exception_table_length at code_start + new_code_length
    etl_pos = idx + new_code_length
    etl = struct.unpack('>H', data[etl_pos:etl_pos+2])[0]
    print(f'Exception table length: {etl}')
    
    # Skip exception table entries and attribute headers
    # Each exception table entry is 8 bytes (start_pc, end_pc, handler_pc, catch_type)
    attr_pos = etl_pos + 2 + (etl * 8)
    
    # Parse attributes
    attrs_count = struct.unpack('>H', data[attr_pos:attr_pos+2])[0]
    print(f'Attributes count: {attrs_count}')
    attr_pos += 2
    
    for a in range(attrs_count):
        attr_name_idx = struct.unpack('>H', data[attr_pos:attr_pos+2])[0]
        attr_len = struct.unpack('>I', data[attr_pos+2:attr_pos+6])[0]
        attr_data_start = attr_pos + 6
        
        print(f'  Attribute {a}: name_index={attr_name_idx}, length={attr_len}')
        
        # Read attribute name from constant pool
        # We need to find the UTF8 constant for this index
        # For now, let's just handle LineNumberTable and StackMapTable
        
        attr_pos = attr_data_start + attr_len
    
else:
    print('Could not find hHhHhHhHhH method')

with open(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\jooon\util\OverlayScreen.class', 'wb') as f:
    f.write(data)
print('Done')
