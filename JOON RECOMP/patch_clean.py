import struct

def skip_vti(data, pos):
    tag = data[pos]
    if tag <= 6:
        return 1
    elif tag in (7, 8):
        return 3
    return 1

# ============================================================
with open(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\jooon\util\OverlayScreen.class', 'rb') as f:
    data = bytearray(f.read())

print(f'Original file size: {len(data)}')

# ============ 1. PATCH gGgGgGgGgG ============
# bipush 7 -> iconst_3, NOP elements 3-6
# Original: 10 07 at 0x38ee
data[0x38ee] = 0x06  # iconst_3
data[0x38ef] = 0x00  # NOP (was 0x07)
# NOP elements 3-6: each element is 8 bytes, starting at 0x390c
# Element 3 starts at 0x390c (offset from bipush: 0x38ee + 30 = 0x390c) ... let me recompute
# header: bipush 7 (2) + anewarray (3) + astore_1 (1) + elem0 (8) + elem1 (8) + elem2 (8) = 30 bytes
# So elem3 starts at 0x38ee + 30 = 0x390c
for i in range(0x390c, 0x390c + 32):
    data[i] = 0x00
print('gGgGgGgGgG: patched to 3 elements (sky)')

# ============ 2. PATCH iIiIiIiIiI ============
# iconst_4 -> iconst_3, change elem1 (109->101), elem2 (111->117), NOP elem3
data[0x3bd0] = 0x06  # iconst_3
data[0x3be0] = 0x65  # 101 = 'e'
data[0x3be8] = 0x75  # 117 = 'u'
# NOP element 3 (8 bytes at 0x3bed)
for i in range(0x3bed, 0x3bf5):
    data[i] = 0x00
print('iIiIiIiIiI: patched to 3 elements (.eu)')

# ============ 3. PATCH hHhHhHhHhH ============
idx = 0x3a70  # code[] start

# --- 3a. Change element values ---
# iconst_3 (06) -> iconst_4 (07)
data[idx] = 0x07
# Element 0: 105 -> 46
data[idx + 8] = 0x2E
# Element 1: 121 -> 50
data[idx + 16] = 0x32
# Element 2: 117 -> 104
data[idx + 24] = 104

# --- 3b. Insert element 3 (8 bytes) at bytecode offset 29 ---
INSERT_BYTES = bytes([
    0x2B, 0x06,          # aload_1, iconst_3
    0x10, 0x7A,          # bipush 122 ('z')
    0xB8, 0x01, 0x2B,    # invokestatic #299
    0x53                 # aastore
])
insert_pos = idx + 29
data[insert_pos:insert_pos] = INSERT_BYTES

# --- 3c. Update code_length (4 bytes at idx-4) ---
old_cl = struct.unpack('>I', data[idx-4:idx])[0]
new_cl = old_cl + 8
data[idx-4:idx] = struct.pack('>I', new_cl)

# --- 3d. Update Code attribute_length (4 bytes at idx-12) ---
# attr header: name_idx(2)+attr_len(4) before max_stack(2)+max_locals(2)+code_length(4)
# So attr_len is at idx-12
old_al = struct.unpack('>I', data[idx-12:idx-8])[0]
new_al = old_al + 8
data[idx-12:idx-8] = struct.pack('>I', new_al)

print(f'hHhHhHhHhH: code_len {old_cl}->{new_cl}, attr_len {old_al}->{new_al}')

# --- 3e. Verify Code attribute name index ---
name_idx = struct.unpack('>H', data[idx-14:idx-12])[0]
print(f'Code attr name_index: {name_idx}')

# --- 3f. Fix inner attributes (LNT, SMT, LVT) ---
etl_pos = idx + new_cl
etl = struct.unpack('>H', data[etl_pos:etl_pos+2])[0]
ac_pos = etl_pos + 2 + etl * 8
attrs_count = struct.unpack('>H', data[ac_pos:ac_pos+2])[0]

# Build CP for name resolution
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

ap = ac_pos + 2
for a in range(attrs_count):
    aname_idx = struct.unpack('>H', data[ap:ap+2])[0]
    alen = struct.unpack('>I', data[ap+2:ap+6])[0]
    adata = ap + 6
    aname = utf8_map.get(aname_idx, f'UNK({aname_idx})')
    print(f'  Inner attr {a}: {aname}, len={alen}')

    if aname == 'LineNumberTable':
        lnt_len = struct.unpack('>H', data[adata:adata+2])[0]
        for e in range(lnt_len):
            ep = adata + 2 + e * 4
            sp = struct.unpack('>H', data[ep:ep+2])[0]
            if sp >= 29:
                old_sp = sp
                new_sp = sp + 8
                data[ep:ep+2] = struct.pack('>H', new_sp)
                ln = struct.unpack('>H', data[ep+2:ep+4])[0]
                print(f'    LNT[{e}]: pc {old_sp} -> {new_sp} (line {ln})')

    elif aname == 'StackMapTable':
        smt_len = struct.unpack('>H', data[adata:adata+2])[0]
        p = adata + 2
        cum = 0
        for e in range(smt_len):
            ft = data[p]
            # Parse frame to get size and delta
            if ft <= 63:
                fsz = 1
                delta = ft
            elif ft <= 127:
                fsz = 1 + skip_vti(data, p+1)
                delta = ft - 64
            elif ft == 247:
                fsz = 3 + skip_vti(data, p+3)
                delta = struct.unpack('>H', data[p+1:p+3])[0]
            elif 248 <= ft <= 250:
                fsz = 3
                delta = struct.unpack('>H', data[p+1:p+3])[0]
            elif ft == 251:
                fsz = 3
                delta = struct.unpack('>H', data[p+1:p+3])[0]
            elif 252 <= ft <= 254:
                n = ft - 251
                fsz = 3
                for _ in range(n):
                    fsz += skip_vti(data, p+fsz)
                delta = struct.unpack('>H', data[p+1:p+3])[0]
            elif ft == 255:
                pp = p + 3
                lc = struct.unpack('>H', data[pp:pp+2])[0]; pp += 2
                for _ in range(lc): pp += skip_vti(data, pp)
                sc = struct.unpack('>H', data[pp:pp+2])[0]; pp += 2
                for _ in range(sc): pp += skip_vti(data, pp)
                fsz = pp - p
                delta = struct.unpack('>H', data[p+1:p+3])[0]
            else:
                fsz = 1
                delta = 0

            if cum + delta >= 29 and delta > 0:
                old_delta = delta
                new_delta = delta + 8
                
                if ft <= 63:
                    if new_delta <= 63:
                        data[p] = new_delta
                    else:
                        # SAME -> SAME_FRAME_EXTENDED
                        data[p:p+1] = bytes([251]) + struct.pack('>H', new_delta)
                elif ft <= 127:
                    if new_delta <= 63:
                        vti_b = data[p+1:p+fsz]
                        new_frame = bytes([64 + new_delta]) + vti_b
                        data[p:p+fsz] = new_frame
                    else:
                        vti_b = data[p+1:p+fsz]
                        new_frame = bytes([247]) + struct.pack('>H', new_delta) + vti_b
                        data[p:p+fsz] = new_frame
                elif ft == 247:
                    data[p+1:p+3] = struct.pack('>H', new_delta)
                elif ft == 251:
                    data[p+1:p+3] = struct.pack('>H', new_delta)
                elif 248 <= ft <= 250:
                    data[p+1:p+3] = struct.pack('>H', new_delta)
                elif 252 <= ft <= 254:
                    data[p+1:p+3] = struct.pack('>H', new_delta)
                elif ft == 255:
                    data[p+1:p+3] = struct.pack('>H', new_delta)
                
                print(f'    SMT[{e}]: delta {old_delta} -> {new_delta} (cum={cum}+{delta}={cum+delta})')
                break
            cum += delta
            p += fsz

    elif aname == 'LocalVariableTable':
        lvt_len = struct.unpack('>H', data[adata:adata+2])[0]
        for e in range(lvt_len):
            ep = adata + 2 + e * 10
            sp = struct.unpack('>H', data[ep:ep+2])[0]
            if sp >= 29:
                data[ep:ep+2] = struct.pack('>H', sp + 8)
                print(f'    LVT[{e}]: pc {sp} -> {sp+8}')

    ap = adata + alen

# ============ Verify ============
v_idx = 0x3a70
print(f'\nVerification:')
print(f'  iconst: {data[v_idx]} (expected 7)')
print(f'  elem0: {data[v_idx+8]} (expected 46=".")')
print(f'  elem1: {data[v_idx+16]} (expected 50="2")')
print(f'  elem2: {data[v_idx+24]} (expected 104="h")')
# elem3 bipush is at v_idx+32 (after insertion)
print(f'  elem3 value: {data[v_idx+32]} (expected 122="z")')
print(f'  code_len: {struct.unpack(">I", data[v_idx-4:v_idx])[0]}')
print(f'  attr_len: {struct.unpack(">I", data[v_idx-12:v_idx-8])[0]}')
print(f'  File size: {len(data)}')

# ============ Save ============
with open(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\jooon\util\OverlayScreen.class', 'wb') as f:
    f.write(data)
print('\nSaved!')
