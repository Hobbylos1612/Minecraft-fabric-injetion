"""Debug CP replacement and field parsing"""
import zipfile, struct, io, os

WORK = r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP'

with zipfile.ZipFile(os.path.join(WORK, 'inputmod_clean2.jar')) as z:
    hc_data = z.read('META-INF/jars/httpclient-4.5.13.jar')

with zipfile.ZipFile(io.BytesIO(hc_data), 'r') as hc:
    data = bytearray(hc.read('org/apache/http/client/methods/HttpPost.class'))

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
        cp.append('long'); i += 9; cp.append(None)
    elif tag == 15:
        cp.append('mh'); i += 4
    elif tag in (16, 19, 20):
        cp.append('tag'); i += 3
    elif tag in (7, 8):
        cp.append(f'#{struct.unpack(">H", data[i+1:i+3])[0]}'); i += 3
    elif tag in (3, 4, 9, 10, 11, 12):
        cp.append('tag'); i += 5
    elif tag in (17, 18):
        cp.append('tag'); i += 5
    else:
        cp.append('?'); i += 3
cp_end = i
print(f'CP: count={orig_cp_count}, cp_end={cp_end}')

# Build new CP
new_cp = bytearray(data[8:cp_end])
new_cp_count = orig_cp_count + 6
struct.pack_into('>H', new_cp, 0, new_cp_count)

idx_u_hc = orig_cp_count
idx_u_rw = orig_cp_count + 1
idx_u_ds = orig_cp_count + 2
idx_c_hc = orig_cp_count + 3
idx_nat = orig_cp_count + 4
idx_mref = orig_cp_count + 5

new_cp += b'\x01' + struct.pack('>H', 39) + b'org/apache/http/client/methods/HttpPostHook'
new_cp += b'\x01' + struct.pack('>H', 7) + b'rewrite'
ds = '(Ljava/lang/String;)Ljava/lang/String;'
new_cp += b'\x01' + struct.pack('>H', len(ds)) + ds.encode()
new_cp += b'\x07' + struct.pack('>H', idx_u_hc)
new_cp += b'\x0c' + struct.pack('>H', idx_u_rw) + struct.pack('>H', idx_u_ds)
new_cp += b'\x0a' + struct.pack('>H', idx_c_hc) + struct.pack('>H', idx_nat)

print(f'New CP: count={new_cp_count}, total_bytes={len(new_cp)}')

# Replace CP
old_cp_size = cp_end - 8
data[8:cp_end] = new_cp
cp_end = 8 + len(new_cp)
print(f'After replacement: cp_end={cp_end}, class_size={len(data)}')

# Parse post-CP
off = cp_end
acc = struct.unpack('>H', data[off:off+2])[0]
this = struct.unpack('>H', data[off+2:off+4])[0]
sup = struct.unpack('>H', data[off+4:off+6])[0]
print(f'access={acc:#x}, this=#{this} ({cp[this]}), super=#{sup} ({cp[sup]})')

off += 8
iface_count = struct.unpack('>H', data[off:off+2])[0]
print(f'interfaces: {iface_count}')
off += 2 + iface_count * 2

field_count = struct.unpack('>H', data[off:off+2])[0]
off += 2
print(f'fields: {field_count}')
for f in range(field_count):
    acc_f = struct.unpack('>H', data[off:off+2])[0]
    nidx_f = struct.unpack('>H', data[off+2:off+4])[0]
    didx_f = struct.unpack('>H', data[off+4:off+6])[0]
    ac_f = struct.unpack('>H', data[off+6:off+8])[0]
    print(f'  field{f}: name={cp[nidx_f]}, desc={cp[didx_f]}, ac={ac_f}')
    p = off + 8
    for a in range(ac_f):
        anidx = struct.unpack('>H', data[p:p+2])[0]
        alen = struct.unpack('>I', data[p+2:p+6])[0]
        print(f'    attr: {cp[anidx]} ({alen} bytes)')
        p += 6 + alen
    off = p
    print(f'    off advances to {off}')

method_count = struct.unpack('>H', data[off:off+2])[0]
print(f'methods: {method_count}')
off += 2
for m in range(method_count):
    macc = struct.unpack('>H', data[off:off+2])[0]
    nidx = struct.unpack('>H', data[off+2:off+4])[0]
    didx = struct.unpack('>H', data[off+4:off+6])[0]
    ac = struct.unpack('>H', data[off+6:off+8])[0]
    print(f'  [{m}] {cp[nidx]}{cp[didx]} acc={macc:#x} ac={ac}')
    p = off + 8
    for a in range(ac):
        anidx = struct.unpack('>H', data[p:p+2])[0]
        alen = struct.unpack('>I', data[p+2:p+6])[0]
        print(f'    attr: {cp[anidx]} ({alen} bytes)')
        if cp[anidx] == 'Code':
            code_len = struct.unpack('>I', data[p+10:p+14])[0]
            print(f'      code: {data[p+14:p+14+code_len].hex()}')
        p += 6 + alen
    off = p
    print(f'    off={off}')
