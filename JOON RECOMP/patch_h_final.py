import struct

# ============================================================
# Helper: skip one verification_type_info, return total bytes consumed
# ============================================================
def skip_vti(data, pos):
    tag = data[pos]
    if tag <= 6:           # Top, Integer, Float, Double, Long, Null, UninitializedThis
        return 1
    elif tag == 7:         # Object
        return 3
    elif tag == 8:         # Uninitialized
        return 3
    else:
        return 1

# ============================================================
# Helper: parse a StackMapTable frame and return (frame_size, delta)
# delta is the offset_delta value for this frame
# ============================================================
def parse_smt_frame(data, pos):
    ft = data[pos]
    if ft <= 63:
        return 1, ft
    elif ft <= 127:
        vti_size = skip_vti(data, pos + 1)
        return 1 + vti_size, ft - 64
    elif ft == 247:
        vti_size = skip_vti(data, pos + 3)
        return 3 + vti_size, struct.unpack('>H', data[pos+1:pos+3])[0]
    elif 248 <= ft <= 250:
        return 3, struct.unpack('>H', data[pos+1:pos+3])[0]
    elif ft == 251:
        return 3, struct.unpack('>H', data[pos+1:pos+3])[0]
    elif 252 <= ft <= 254:
        n = ft - 251
        p = pos + 3
        for _ in range(n):
            p += skip_vti(data, p)
        return p - pos, struct.unpack('>H', data[pos+1:pos+3])[0]
    elif ft == 255:
        p = pos + 3
        locals_count = struct.unpack('>H', data[p:p+2])[0]
        p += 2
        for _ in range(locals_count):
            p += skip_vti(data, p)
        stack_count = struct.unpack('>H', data[p:p+2])[0]
        p += 2
        for _ in range(stack_count):
            p += skip_vti(data, p)
        return p - pos, struct.unpack('>H', data[pos+1:pos+3])[0]
    return 0, 0

# ============================================================
# Helper: rewrite a frame's delta, returning new bytes
# ============================================================
def rewrite_frame_delta(data, pos, old_delta, new_delta):
    ft = data[pos]
    if ft <= 63:
        if new_delta <= 63:
            return bytes([new_delta])
        else:
            return bytes([251]) + struct.pack('>H', new_delta)
    elif ft <= 127:
        if new_delta <= 63:
            return bytes([64 + new_delta]) + data[pos+1:pos+skip_vti(data, pos)]
        else:
            vti = data[pos+1:pos+skip_vti(data, pos)]
            return bytes([247, new_delta >> 8, new_delta & 0xFF]) + vti
    elif ft == 247:
        vti = data[pos+3:pos+skip_vti(data, pos)]
        return bytes([247, new_delta >> 8, new_delta & 0xFF]) + vti
    elif 248 <= ft <= 250:
        return bytes([ft, new_delta >> 8, new_delta & 0xFF])
    elif ft == 251:
        return bytes([251, new_delta >> 8, new_delta & 0xFF])
    elif 252 <= ft <= 254:
        n = ft - 251
        p = pos + 3
        vtis = b''
        for _ in range(n):
            vtis += data[p:p+skip_vti(data, p)]
            p += skip_vti(data, p)
        return bytes([ft, new_delta >> 8, new_delta & 0xFF]) + vtis
    elif ft == 255:
        p = pos + 3
        locals_count = struct.unpack('>H', data[p:p+2])[0]
        p += 2
        locals_vtis = b''
        for _ in range(locals_count):
            s = skip_vti(data, p)
            locals_vtis += data[p:p+s]
            p += s
        stack_count = struct.unpack('>H', data[p:p+2])[0]
        p += 2
        stack_vtis = b''
        for _ in range(stack_count):
            s = skip_vti(data, p)
            stack_vtis += data[p:p+s]
            p += s
        return (bytes([255, new_delta >> 8, new_delta & 0xFF]) +
                struct.pack('>H', locals_count) + locals_vtis +
                struct.pack('>H', stack_count) + stack_vtis)
    return b''

# ============================================================
# Main patching logic
# ============================================================
with open(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\jooon\util\OverlayScreen.class', 'rb') as f:
    data = bytearray(f.read())

idx = 0x3a70

# --- Parse attribute info BEFORE any modifications ---
code_len = struct.unpack('>I', data[idx-4:idx])[0]
print(f'code_length = {code_len}')

etl_pos = idx + code_len
etl = struct.unpack('>H', data[etl_pos:etl_pos+2])[0]
attr_pos = etl_pos + 2 + etl * 8
attrs_count = struct.unpack('>H', data[attr_pos:attr_pos+2])[0]

# Parse CP for names
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

# Store attribute info (absolute positions BEFORE modification)
attr_infos = []
ap = attr_pos + 2
for _ in range(attrs_count):
    name_idx = struct.unpack('>H', data[ap:ap+2])[0]
    attr_len = struct.unpack('>I', data[ap+2:ap+6])[0]
    a_data = ap + 6
    name = utf8_map.get(name_idx, f'UNK({name_idx})')
    attr_infos.append({
        'header_pos': ap,
        'name': name,
        'len': attr_len,
        'data_pos': a_data
    })
    print(f'  Attr: name={name}, len={attr_len}, data_pos={hex(a_data)}')
    ap = a_data + attr_len

# --- Apply modifications to bytecode ---
INSERT_OFFSET = 29

# Change element 2: 117 -> 104
data[idx + 24] = 104
print('Element 2: 117 -> 104')

# Insert element 3 (8 bytes)
ELEMENT3 = bytes([
    0x2B, 0x06,
    0x10, 0x7A,
    0xB8, 0x01, 0x2B,
    0x53
])
insert_file_pos = idx + INSERT_OFFSET
data[insert_file_pos:insert_file_pos] = ELEMENT3
print(f'Inserted element 3 at file offset {hex(insert_file_pos)}')

# Update code_length
new_code_len = code_len + 8
data[idx-4:idx] = struct.pack('>I', new_code_len)
print(f'code_length: {code_len} -> {new_code_len}')

# --- Now fix LNT and SMT at their shifted positions ---
# After insertion, all file offsets >= insert_file_pos (= idx+29) shifted by +8
# So each attribute's data_pos is now old_data_pos + 8 (since attr comes after code)

SHIFT = 8

for attr in attr_infos:
    old_dp = attr['data_pos']
    new_dp = old_dp + SHIFT  # shifted by 8 after insertion

    if attr['name'] == 'LineNumberTable':
        lnt_len = struct.unpack('>H', data[new_dp:new_dp+2])[0]
        for e in range(lnt_len):
            entry_pos = new_dp + 2 + e * 4
            start_pc = struct.unpack('>H', data[entry_pos:entry_pos+2])[0]
            if start_pc >= INSERT_OFFSET:
                new_sp = start_pc + SHIFT
                data[entry_pos:entry_pos+2] = struct.pack('>H', new_sp)
                line_no = struct.unpack('>H', data[entry_pos+2:entry_pos+4])[0]
                print(f'  LNT[{e}]: pc {start_pc} -> {new_sp} (line {line_no})')

    elif attr['name'] == 'StackMapTable':
        smt_len = struct.unpack('>H', data[new_dp:new_dp+2])[0]
        print(f'StackMapTable: {smt_len} entries')

        p = new_dp + 2
        # We need to find the first frame whose cumulative_offset >= INSERT_OFFSET
        # and add SHIFT to its delta.
        # Since we're working with the already-shifted data, we need to compute
        # cumulative offsets using the unshifted code. But the deltas stored in SMT
        # are still the original deltas (we haven't modified SMT yet).
        # So we compute cumulative_offset from the original deltas.
        cum = 0
        for e in range(smt_len):
            frame_size, delta = parse_smt_frame(data, p)
            if cum + delta >= INSERT_OFFSET:
                # This frame's cumulative offset crosses the insertion boundary.
                # Its offset_delta needs to be increased by SHIFT.
                old_delta = delta
                new_delta = old_delta + SHIFT
                new_frame_bytes = rewrite_frame_delta(data, p, old_delta, new_delta)
                data[p:p+frame_size] = new_frame_bytes
                print(f'  SMT[{e}]: delta {old_delta} -> {new_delta} (cum={cum}+{delta}={cum+delta})')
                # All subsequent frames are fine (their deltas are relative)
                break
            cum += delta
            p += frame_size

# --- Save ---
with open(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\jooon\util\OverlayScreen.class', 'wb') as f:
    f.write(data)

print('Saved. Verifying...')

# Verify with some checks
with open(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\jooon\util\OverlayScreen.class', 'rb') as f:
    verify = f.read()

v_idx = 0x3a70
v_code_len = struct.unpack('>I', verify[v_idx-4:v_idx])[0]
print(f'New code_length: {v_code_len}')
print(f'iconst: {verify[v_idx]}')
print(f'Element 0 value: {verify[v_idx+8]}')
print(f'Element 1 value: {verify[v_idx+16]}')
print(f'Element 2 value: {verify[v_idx+24]}')
# Element 3 should be at idx+29 now (no wait, it was inserted at idx+29,
# so element 3's bipush value is at idx+32)
print(f'Element 3 value (bipush): {verify[v_idx+32]}')
print('Done!')
