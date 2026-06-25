"""
Restore inputmod_clean2.jar's httpclient to original, then re-patch.
"""
import zipfile, struct, io, os, shutil, tempfile

WORK = r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP'
MOD_JAR = os.path.join(WORK, 'inputmod_clean2.jar')
ORIG_JAR = os.path.join(WORK, 'inputmod.jar')
HOOK_CLASS = os.path.join(WORK, 'org/apache/http/client/methods/HttpPostHook.class')
HC_INTERNAL = 'META-INF/jars/httpclient-4.5.13.jar'

# Step 1: Extract fresh httpclient from original mod JAR
with zipfile.ZipFile(ORIG_JAR, 'r') as z:
    orig_hc = z.read(HC_INTERNAL)

# Step 2: Extract fresh HttpPost.class
with zipfile.ZipFile(io.BytesIO(orig_hc), 'r') as hc:
    data = bytearray(hc.read('org/apache/http/client/methods/HttpPost.class'))
print(f'Original HttpPost.class: {len(data)} bytes')

# Step 3: Read hook
with open(HOOK_CLASS, 'rb') as f:
    hook_bytes = f.read()
print(f'Hook: {len(hook_bytes)} bytes')

# Step 4: Parse original CP
orig_cp_count = struct.unpack('>H', data[8:10])[0]
i = 10
cp = [None]
while len(cp) < orig_cp_count:
    tag = data[i]
    if tag == 1:
        length = struct.unpack('>H', data[i+1:i+3])[0]
        val = data[i+3:i+3+length].decode('utf-8', errors='replace')
        cp.append(val)
        i += 3 + length
    elif tag in (5, 6):
        cp.append(f'long{tag}'); i += 9; cp.append(None)
    elif tag == 15:
        cp.append('mh'); i += 4
    elif tag in (16, 19, 20):
        cp.append(f'tag'); i += 3
    elif tag in (7, 8):
        cp.append(f'#{struct.unpack(">H", data[i+1:i+3])[0]}'); i += 3
    else:
        cp.append('x'); i += 5
cp_end = i
print(f'CP: {orig_cp_count} entries, ends at {cp_end}')

# Step 5: Build new CP
new_cp = bytearray(data[8:cp_end])
new_cp_count = orig_cp_count + 6
struct.pack_into('>H', new_cp, 0, new_cp_count)

idx_u_hc = orig_cp_count
idx_u_rw = orig_cp_count + 1
idx_u_ds = orig_cp_count + 2
idx_c_hc = orig_cp_count + 3
idx_nat = orig_cp_count + 4
idx_mref = orig_cp_count + 5

s = 'org/apache/http/client/methods/HttpPostHook'
new_cp += b'\x01' + struct.pack('>H', len(s)) + s.encode()
new_cp += b'\x01' + struct.pack('>H', 7) + b'rewrite'
ds = '(Ljava/lang/String;)Ljava/lang/String;'
new_cp += b'\x01' + struct.pack('>H', len(ds)) + ds.encode()
new_cp += b'\x07' + struct.pack('>H', idx_u_hc)
new_cp += b'\x0c' + struct.pack('>H', idx_u_rw) + struct.pack('>H', idx_u_ds)
new_cp += b'\x0a' + struct.pack('>H', idx_c_hc) + struct.pack('>H', idx_nat)

print(f'New CP count: {new_cp_count}, mref idx: {idx_mref}')

# Step 6: Replace CP
old_cp_size = cp_end - 8
data[8:cp_end] = new_cp
cp_end = 8 + len(new_cp)

# Step 7: Find string constructor and patch
off = cp_end
off += 6
iface_count = struct.unpack('>H', data[off:off+2])[0]
off += 2 + iface_count * 2

field_count = struct.unpack('>H', data[off:off+2])[0]
off += 2
for _ in range(field_count):
    ac = struct.unpack('>H', data[off+6:off+8])[0]
    p = off + 8
    for _ in range(ac):
        alen = struct.unpack('>I', data[p+2:p+6])[0]
        p += 6 + alen
    off = p

method_count = struct.unpack('>H', data[off:off+2])[0]
off += 2
patched = False
for m in range(method_count):
    nidx = struct.unpack('>H', data[off+2:off+4])[0]
    didx = struct.unpack('>H', data[off+4:off+6])[0]
    ac = struct.unpack('>H', data[off+6:off+8])[0]
    m_name = cp[nidx] if nidx < len(cp) else ''
    m_desc = cp[didx] if didx < len(cp) else ''
    p = off + 8
    for a in range(ac):
        anidx = struct.unpack('>H', data[p:p+2])[0]
        alen = struct.unpack('>I', data[p+2:p+6])[0]
        aname = cp[anidx] if anidx < len(cp) else ''
        if aname == 'Code' and m_name == '<init>' and m_desc == '(Ljava/lang/String;)V':
            code_len_off = p + 10
            code_len = struct.unpack('>I', data[code_len_off:code_len_off+4])[0]
            code_body = p + 14
            attr_len_off = p + 2

            print(f'Found ctor: code_body={code_body}, code_len={code_len}')
            print(f'  Original code: {data[code_body:code_body+code_len].hex()}')

            # Insert after: 2a b7 00 01 2a 2b (6 bytes, 0-indexed positions 0-5)
            # Goal: 2a b7 00 01 2a 2b b8 mref b8 00 03 b6 00 02 b1
            insert_at = code_body + 6
            insn = bytes([0xb8, (idx_mref >> 8) & 0xff, idx_mref & 0xff])

            for _ in range(3):
                data.insert(insert_at, 0)
            data[insert_at:insert_at+3] = insn

            new_code_len = code_len + 3
            struct.pack_into('>I', data, code_len_off, new_code_len)
            old_attr_len = struct.unpack('>I', data[attr_len_off:attr_len_off+4])[0]
            struct.pack_into('>I', data, attr_len_off, old_attr_len + 3)

            print(f'  Patched code: {data[code_body:code_body+new_code_len].hex()}')
            patched = True
            break
        p += 6 + alen
    if patched:
        break
    p = off + 8
    for a in range(ac):
        alen = struct.unpack('>I', data[p+2:p+6])[0]
        p += 6 + alen
    off = p

if not patched:
    print('ERROR: String constructor not found!')
    exit(1)

# Step 8: Rebuild httpclient jar
buf = io.BytesIO()
with zipfile.ZipFile(io.BytesIO(orig_hc), 'r') as zin:
    with zipfile.ZipFile(buf, 'w', zipfile.ZIP_DEFLATED) as zout:
        for item in zin.infolist():
            if item.filename == 'org/apache/http/client/methods/HttpPost.class':
                zout.writestr(item, bytes(data))
            else:
                zout.writestr(item, zin.read(item.filename))
        zout.writestr('org/apache/http/client/methods/HttpPostHook.class', hook_bytes)

new_hc = buf.getvalue()
print(f'\nNew httpclient jar: {len(new_hc)} bytes (was {len(orig_hc)})')

# Step 9: Verify the new jar
with zipfile.ZipFile(io.BytesIO(new_hc)) as z:
    names = [n.filename for n in z.infolist()]
    print(f'Hook present: {"HttpPostHook.class" in names}')
    hp = z.read('org/apache/http/client/methods/HttpPost.class')
    needle = bytes([0xb8, (idx_mref >> 8) & 0xff, idx_mref & 0xff])
    if needle in hp:
        print('Hook call PRESENT in bytecode')
        # Find and print the code
        i = 10
        tmp = [None]
        while len(tmp) < new_cp_count:
            tag = hp[i]
            if tag == 1:
                length = struct.unpack('>H', hp[i+1:i+3])[0]
                val = hp[i+3:i+3+length].decode('utf-8', errors='replace')
                tmp.append(val)
                i += 3 + length
            elif tag in (5, 6):
                tmp.append(''); i += 9; tmp.append(None)
            elif tag == 15:
                tmp.append(''); i += 4
            elif tag in (16, 19, 20):
                tmp.append(''); i += 3
            elif tag in (7, 8):
                tmp.append(''); i += 3
            else:
                tmp.append(''); i += 5
        cpe = i
        off = cpe + 6
        iface_n = struct.unpack('>H', hp[off:off+2])[0]
        off += 2 + iface_n * 2
        f_n = struct.unpack('>H', hp[off:off+2])[0]
        off += 2
        for _ in range(f_n):
            ac = struct.unpack('>H', hp[off+6:off+8])[0]
            p = off + 8
            for _ in range(ac):
                alen = struct.unpack('>I', hp[p+2:p+6])[0]
                p += 6 + alen
            off = p
        m_n = struct.unpack('>H', hp[off:off+2])[0]
        off += 2
        for m in range(m_n):
            nidx = struct.unpack('>H', hp[off+2:off+4])[0]
            didx = struct.unpack('>H', hp[off+4:off+6])[0]
            ac = struct.unpack('>H', hp[off+6:off+8])[0]
            mn = tmp[nidx] if nidx < len(tmp) else ''
            md = tmp[didx] if didx < len(tmp) else ''
            p = off + 8
            for a in range(ac):
                anidx = struct.unpack('>H', hp[p:p+2])[0]
                alen = struct.unpack('>I', hp[p+2:p+6])[0]
                an = tmp[anidx] if anidx < len(tmp) else ''
                if an == 'Code' and mn == '<init>' and md == '(Ljava/lang/String;)V':
                    cl = struct.unpack('>I', hp[p+10:p+14])[0]
                    print(f'  Constructor code ({cl} bytes): {hp[p+14:p+14+cl].hex()}')
                p += 6 + alen
            off = p
    else:
        print('Hook call MISSING!')
        
# Step 10: Replace in mod JAR
fd, tmp = tempfile.mkstemp(suffix='.jar')
os.close(fd)
with zipfile.ZipFile(MOD_JAR, 'r') as zin:
    with zipfile.ZipFile(tmp, 'w', zipfile.ZIP_DEFLATED) as zout:
        for item in zin.infolist():
            if item.filename == HC_INTERNAL:
                zout.writestr(item, new_hc)
            else:
                zout.writestr(item, zin.read(item.filename))
shutil.move(tmp, MOD_JAR)
print(f'\nUpdated {MOD_JAR}')
