import struct

def skip_vti(data, pos):
    """Return size (in bytes) of a verification_type_info at pos"""
    tag = data[pos]
    if tag <= 6:    # Top, Integer, Float, Double, Long, Null, UninitializedThis
        return 1
    elif tag == 7:  # Object
        return 3
    elif tag == 8:  # Uninitialized
        return 3
    return 1

def parse_smt_frame(data, pos):
    """Parse an SMT frame, return (frame_total_size, offset_delta)"""
    ft = data[pos]
    if ft <= 63:
        return 1, ft
    elif ft <= 127:
        return 1 + skip_vti(data, pos + 1), ft - 64
    elif ft == 247:
        d = struct.unpack('>H', data[pos+1:pos+3])[0]
        return 3 + skip_vti(data, pos + 3), d
    elif 248 <= ft <= 250:
        return 3, struct.unpack('>H', data[pos+1:pos+3])[0]
    elif ft == 251:
        return 3, struct.unpack('>H', data[pos+1:pos+3])[0]
    elif 252 <= ft <= 254:
        n = ft - 251
        sz = 3
        for _ in range(n):
            sz += skip_vti(data, pos + sz)
        return sz, struct.unpack('>H', data[pos+1:pos+3])[0]
    elif ft == 255:
        p = pos + 3
        lc = struct.unpack('>H', data[p:p+2])[0]; p += 2
        for _ in range(lc):
            p += skip_vti(data, p)
        sc = struct.unpack('>H', data[p:p+2])[0]; p += 2
        for _ in range(sc):
            p += skip_vti(data, p)
        return p - pos, struct.unpack('>H', data[pos+1:pos+3])[0]
    return 0, 0

# ============================================================
with open(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\jooon\util\OverlayScreen.class', 'rb') as f:
    data = bytearray(f.read())

print('gGgGgGgGgG and iIiIiIiIiI: already patched in fresh extract, skipping')

# ============ hHhHhHhHhH PATCH ============
idx = 0x3a70  # code[] start

# --- Verify current state ---
assert data[idx] == 0x07, 'hHhHhHhHhH: expected iconst_4'
print('hHhHhHhHhH: current iconst_4, values 46,50,117')

# --- Change element 2: 117 -> 104 ---
data[idx + 24] = 104

# --- Insert element 3 (8 bytes) at bytecode offset 29 ---
INSERT_OFFSET = 29
E3 = bytes([
    0x2B, 0x06,
    0x10, 0x7A,
    0xB8, 0x01, 0x2B,
    0x53
])
insert_file_pos = idx + INSERT_OFFSET
data[insert_file_pos:insert_file_pos] = E3
print(f'Inserted element 3 at file offset {hex(insert_file_pos)}')

# --- Update code_length (4 bytes at idx-4) ---
old_code_len = struct.unpack('>I', data[idx-4:idx])[0]
new_code_len = old_code_len + 8
data[idx-4:idx] = struct.pack('>I', new_code_len)
print(f'code_length: {old_code_len} -> {new_code_len}')

# --- Update Code attribute_length (4 bytes at idx-8) ---
old_attr_len = struct.unpack('>I', data[idx-8:idx-4])[0]
new_attr_len = old_attr_len + 8
data[idx-8:idx-4] = struct.pack('>I', new_attr_len)
print(f'Code attr_length: {old_attr_len} -> {new_attr_len}')

# --- Parse inner attributes (at shifted positions) ---
# etl_pos = idx + new_code_len (after insertion)
etl_pos = idx + new_code_len
etl = struct.unpack('>H', data[etl_pos:etl_pos+2])[0]
ac_pos = etl_pos + 2 + etl * 8
attrs_count = struct.unpack('>H', data[ac_pos:ac_pos+2])[0]
print(f'Inner attributes: count={attrs_count}')

# Build CP to resolve names
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

# Iterate inner attributes
ap = ac_pos + 2
for a in range(attrs_count):
    aname_idx = struct.unpack('>H', data[ap:ap+2])[0]
    alen = struct.unpack('>I', data[ap+2:ap+6])[0]
    adata = ap + 6
    aname = utf8_map.get(aname_idx, f'UNK({aname_idx})')
    print(f'  Attr {a}: name={aname}, len={alen}, data_pos={hex(adata)}')

    if aname == 'LineNumberTable':
        lnt_len = struct.unpack('>H', data[adata:adata+2])[0]
        for e in range(lnt_len):
            ep = adata + 2 + e * 4
            sp = struct.unpack('>H', data[ep:ep+2])[0]
            if sp >= INSERT_OFFSET:
                new_sp = sp + 8
                data[ep:ep+2] = struct.pack('>H', new_sp)
                ln = struct.unpack('>H', data[ep+2:ep+4])[0]
                print(f'    LNT[{e}]: pc {sp} -> {new_sp} (line {ln})')

    elif aname == 'StackMapTable':
        smt_len = struct.unpack('>H', data[adata:adata+2])[0]
        print(f'    SMT: {smt_len} entries')
        p = adata + 2
        cum = 0
        for e in range(smt_len):
            fsz, delta = parse_smt_frame(data, p)
            if cum + delta >= INSERT_OFFSET:
                old_sz = fsz
                # Build new frame with adjusted delta
                ft = data[p]
                vti_data = data[p+3:p+fsz] if ft >= 252 else b''
                
                new_delta = delta + 8
                
                if ft <= 63:
                    if new_delta <= 63:
                        new_frame = bytes([new_delta])
                    else:
                        new_frame = bytes([251]) + struct.pack('>H', new_delta)
                elif ft <= 127:
                    vti = data[p+1:p+fsz]
                    if new_delta <= 63:
                        new_frame = bytes([64 + new_delta]) + vti
                    else:
                        new_frame = bytes([247]) + struct.pack('>H', new_delta) + vti
                elif ft == 247:
                    new_frame = bytes([247]) + struct.pack('>H', new_delta) + data[p+3:p+fsz]
                elif ft == 251:
                    new_frame = bytes([251]) + struct.pack('>H', new_delta)
                elif 248 <= ft <= 250:
                    new_frame = bytes([ft]) + struct.pack('>H', new_delta)
                elif 252 <= ft <= 254:
                    n = ft - 251
                    p2 = p + 3
                    vtis = b''
                    for _ in range(n):
                        vs = skip_vti(data, p2)
                        vtis += data[p2:p2+vs]
                        p2 += vs
                    new_frame = bytes([ft]) + struct.pack('>H', new_delta) + vtis
                elif ft == 255:
                    p2 = p + 3
                    lc = struct.unpack('>H', data[p2:p2+2])[0]; p2 += 2
                    vtis = b''
                    for _ in range(lc):
                        vs = skip_vti(data, p2)
                        vtis += data[p2:p2+vs]
                        p2 += vs
                    sc = struct.unpack('>H', data[p2:p2+2])[0]; p2 += 2
                    for _ in range(sc):
                        vs = skip_vti(data, p2)
                        vtis += data[p2:p2+vs]
                        p2 += vs
                    new_frame = bytes([255]) + struct.pack('>H', new_delta) + data[p+3:p2]
                
                data[p:p+fsz] = new_frame
                print(f'    SMT[{e}]: delta {delta} -> {new_delta} (cum={cum}+{delta}={cum+delta})')
                break
            cum += delta
            p += fsz

    elif aname == 'LocalVariableTable':
        lvt_len = struct.unpack('>H', data[adata:adata+2])[0]
        for e in range(lvt_len):
            ep = adata + 2 + e * 10
            sp = struct.unpack('>H', data[ep:ep+2])[0]
            if sp >= INSERT_OFFSET:
                new_sp = sp + 8
                data[ep:ep+2] = struct.pack('>H', new_sp)
            # Also check length
            length = struct.unpack('>H', data[ep+2:ep+4])[0]
            if sp + length > INSERT_OFFSET:
                # The length may also need adjustment if it extends past the insertion
                # but since we inserted code, variables' scope might extend
                pass

    ap = adata + alen

# ============ Verify ============
v_idx = 0x3a70
v_cl = struct.unpack('>I', data[v_idx-4:v_idx])[0]
print(f'\nVerification:')
print(f'  code_length: {v_cl}')
print(f'  iconst: {data[v_idx]}')
print(f'  elem0: {data[v_idx+8]}')
print(f'  elem1: {data[v_idx+16]}')
print(f'  elem2: {data[v_idx+24]}')
print(f'  elem3: {data[v_idx+32]}')
v_al = struct.unpack('>I', data[v_idx-8:v_idx-4])[0]
print(f'  Code attr_length: {v_al}')
print(f'  file size: {len(data)}')

# ============ Save ============
with open(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\jooon\util\OverlayScreen.class', 'wb') as f:
    f.write(data)
print('\nSaved!')
