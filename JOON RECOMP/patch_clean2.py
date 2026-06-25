import struct

def skip_vti(data, pos):
    tag = data[pos]
    if tag <= 6:
        return 1
    elif tag in (7, 8):
        return 3
    return 1

# Known CP indices (verified from class file)
CP_CODE = 538
CP_LINE_NUMBER_TABLE = 539
CP_LOCAL_VARIABLE_TABLE = 540
CP_STACK_MAP_TABLE = 541

with open(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\jooon\util\OverlayScreen.class', 'rb') as f:
    data = bytearray(f.read())

print(f'Original file size: {len(data)}')

# ============ 1. PATCH gGgGgGgGgG ============
data[0x38ee] = 0x06  # iconst_3
data[0x38ef] = 0x00  # NOP
for i in range(0x390c, 0x390c + 32):
    data[i] = 0x00
print('gGgGgGgGgG: patched to 3 elements (sky)')

# ============ 2. PATCH iIiIiIiIiI ============
data[0x3bd0] = 0x06  # iconst_3
data[0x3be0] = 0x65  # 101 = 'e'
data[0x3be8] = 0x75  # 117 = 'u'
for i in range(0x3bed, 0x3bf5):
    data[i] = 0x00
print('iIiIiIiIiI: patched to 3 elements (.eu)')

# ============ 3. PATCH hHhHhHhHhH ============
idx = 0x3a70

# --- Change element values ---
data[idx] = 0x07       # iconst_4
data[idx + 8] = 0x2E   # 46 = '.'
data[idx + 16] = 0x32  # 50 = '2'
data[idx + 24] = 104    # 'h'

# --- Insert element 3 at bytecode offset 29 ---
INSERT = bytes([
    0x2B, 0x06, 0x10, 0x7A, 0xB8, 0x01, 0x2B, 0x53
])
insert_pos = idx + 29
data[insert_pos:insert_pos] = INSERT

# --- Update code_length (4 bytes at idx-4) ---
old_cl = struct.unpack('>I', data[idx-4:idx])[0]
new_cl = old_cl + 8
data[idx-4:idx] = struct.pack('>I', new_cl)

# --- Update Code attribute_length (4 bytes at idx-12) ---
old_al = struct.unpack('>I', data[idx-12:idx-8])[0]
new_al = old_al + 8
data[idx-12:idx-8] = struct.pack('>I', new_al)

print(f'hHhHhHhHhH: code_len {old_cl}->{new_cl}, attr_len {old_al}->{new_al}')

# --- Fix inner attributes ---
etl_pos = idx + new_cl
etl = struct.unpack('>H', data[etl_pos:etl_pos+2])[0]
ac_pos = etl_pos + 2 + etl * 8
attrs_count = struct.unpack('>H', data[ac_pos:ac_pos+2])[0]

ap = ac_pos + 2
for a in range(attrs_count):
    aname_idx = struct.unpack('>H', data[ap:ap+2])[0]
    alen = struct.unpack('>I', data[ap+2:ap+6])[0]
    adata = ap + 6
    
    name = {CP_CODE: 'Code', CP_LINE_NUMBER_TABLE: 'LineNumberTable',
            CP_LOCAL_VARIABLE_TABLE: 'LocalVariableTable',
            CP_STACK_MAP_TABLE: 'StackMapTable'}.get(aname_idx, f'?({aname_idx})')
    print(f'  Inner attr {a}: {name}, len={alen}')

    if aname_idx == CP_LINE_NUMBER_TABLE:
        lnt_len = struct.unpack('>H', data[adata:adata+2])[0]
        for e in range(lnt_len):
            ep = adata + 2 + e * 4
            sp = struct.unpack('>H', data[ep:ep+2])[0]
            if sp >= 29:
                data[ep:ep+2] = struct.pack('>H', sp + 8)
                ln = struct.unpack('>H', data[ep+2:ep+4])[0]
                print(f'    LNT[{e}]: pc {sp} -> {sp+8} (line {ln})')

    elif aname_idx == CP_STACK_MAP_TABLE:
        smt_len = struct.unpack('>H', data[adata:adata+2])[0]
        p = adata + 2
        cum = 0
        for e in range(smt_len):
            ft = data[p]
            # Parse frame
            if ft <= 63:
                fsz = 1; delta = ft
            elif ft <= 127:
                fsz = 1 + skip_vti(data, p+1); delta = ft - 64
            elif ft == 247:
                fsz = 3 + skip_vti(data, p+3)
                delta = struct.unpack('>H', data[p+1:p+3])[0]
            elif 248 <= ft <= 250:
                fsz = 3; delta = struct.unpack('>H', data[p+1:p+3])[0]
            elif ft == 251:
                fsz = 3; delta = struct.unpack('>H', data[p+1:p+3])[0]
            elif 252 <= ft <= 254:
                n = ft - 251; fsz = 3
                for _ in range(n): fsz += skip_vti(data, p+fsz)
                delta = struct.unpack('>H', data[p+1:p+3])[0]
            elif ft == 255:
                pp = p + 3
                lc = struct.unpack('>H', data[pp:pp+2])[0]; pp += 2
                for _ in range(lc): pp += skip_vti(data, pp)
                sc = struct.unpack('>H', data[pp:pp+2])[0]; pp += 2
                for _ in range(sc): pp += skip_vti(data, pp)
                fsz = pp - p; delta = struct.unpack('>H', data[p+1:p+3])[0]
            else:
                fsz = 1; delta = 0

            if cum + delta >= 29:
                old_delta = delta
                new_delta = delta + 8
                
                if ft <= 63:
                    if new_delta <= 63:
                        data[p] = new_delta
                    else:
                        data[p:p+1] = bytes([251]) + struct.pack('>H', new_delta)
                elif ft <= 127:
                    vti = data[p+1:p+fsz]
                    if new_delta <= 63:
                        data[p:p+fsz] = bytes([64 + new_delta]) + vti
                    else:
                        data[p:p+fsz] = bytes([247]) + struct.pack('>H', new_delta) + vti
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
                
                print(f'    SMT[{e}]: delta {old_delta} -> {new_delta}')
                break
            cum += delta
            p += fsz

    elif aname_idx == CP_LOCAL_VARIABLE_TABLE:
        lvt_len = struct.unpack('>H', data[adata:adata+2])[0]
        for e in range(lvt_len):
            ep = adata + 2 + e * 10
            start_pc = struct.unpack('>H', data[ep:ep+2])[0]
            if start_pc >= 29:
                data[ep:ep+2] = struct.pack('>H', start_pc + 8)
                print(f'    LVT[{e}]: start_pc {start_pc} -> {start_pc+8}')

    ap = adata + alen

# ============ Verify ============
v_idx = 0x3a70
print(f'\nVerification:')
print(f'File size: {len(data)}')
print(f'iconst: {data[v_idx]}')
print(f'elem0: {data[v_idx+8]} = {chr(data[v_idx+8])}')
print(f'elem1: {data[v_idx+16]} = {chr(data[v_idx+16])}')
print(f'elem2: {data[v_idx+24]} = {chr(data[v_idx+24])}')
print(f'elem3: {data[v_idx+32]} = {chr(data[v_idx+32])}')
print(f'code_len: {struct.unpack(">I", data[v_idx-4:v_idx])[0]}')
print(f'attr_len: {struct.unpack(">I", data[v_idx-12:v_idx-8])[0]}')

# Verify LNT/SMT are still parseable after our changes
etl_pos2 = v_idx + new_cl
etl2 = struct.unpack('>H', data[etl_pos2:etl_pos2+2])[0]
print(f'Exception table length: {etl2}')

with open(r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP\jooon\util\OverlayScreen.class', 'wb') as f:
    f.write(data)
print('\nSaved!')
