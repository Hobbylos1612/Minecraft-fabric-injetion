"""Debug original CP parsing"""
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
    entry_start = i
    if tag == 1:
        length = struct.unpack('>H', data[i+1:i+3])[0]
        val = data[i+3:i+3+length].decode('utf-8', errors='replace')
        cp.append(val)
        i += 3 + length
    elif tag in (5, 6):
        cp.append(f'LONG@{entry_start}')
        i += 9
        cp.append(None)
    elif tag == 15:
        cp.append(f'MH@{entry_start}')
        i += 4
    elif tag in (16, 19, 20):
        cp.append(f'MTHD@{entry_start}')
        i += 3
    elif tag in (7, 8):
        ref = struct.unpack('>H', data[i+1:i+3])[0]
        cp.append(f'Class/Str->#{ref}')
        i += 3
    elif tag in (3, 4):
        val = struct.unpack('>I', data[i+1:i+5])[0]
        cp.append(f'Int/Float:{val}')
        i += 5
    elif tag in (9, 10, 11):
        cidx = struct.unpack('>H', data[i+1:i+3])[0]
        nidx = struct.unpack('>H', data[i+3:i+5])[0]
        cp.append(f'Ref c=#{cidx} n=#{nidx}')
        i += 5
    elif tag == 12:
        nidx = struct.unpack('>H', data[i+1:i+3])[0]
        didx = struct.unpack('>H', data[i+3:i+5])[0]
        cp.append(f'NAT n=#{nidx} d=#{didx}')
        i += 5
    elif tag == 18:
        nidx = struct.unpack('>H', data[i+1:i+3])[0]
        cp.append(f'Idx->#{nidx}')
        i += 3
    else:
        cp.append(f'?tag={tag}@{entry_start}')
        i += 3

cp_end = i
print(f'CP count: {orig_cp_count}, cp_end: {cp_end}')
for idx, val in enumerate(cp):
    if val is not None:
        print(f'  CP#{idx}: {val}')

print('\n--- Post-CP parsing ---')
off = cp_end
acc = struct.unpack('>H', data[off:off+2])[0]
this = struct.unpack('>H', data[off+2:off+4])[0]
sup = struct.unpack('>H', data[off+4:off+6])[0]
print(f'access={acc:#x}, this=#{this} ({cp[this]}), super=#{sup} ({cp[sup]})')

off += 8
iface_count = struct.unpack('>H', data[off:off+2])[0]
off += 2 + iface_count * 2
print(f'interfaces: {iface_count}')

field_count = struct.unpack('>H', data[off:off+2])[0]
off += 2
print(f'fields: {field_count}')
for f in range(field_count):
    acc_f = struct.unpack('>H', data[off:off+2])[0]
    nidx_f = struct.unpack('>H', data[off+2:off+4])[0]
    didx_f = struct.unpack('>H', data[off+4:off+6])[0]
    ac_f = struct.unpack('>H', data[off+6:off+8])[0]
    nv = cp[nidx_f] if nidx_f < len(cp) else '?'
    dv = cp[didx_f] if didx_f < len(cp) else '?'
    print(f'  field{f}: acc={acc_f:#x} name_idx={nidx_f} ({nv}) desc_idx={didx_f} ({dv}) ac={ac_f}')
    p = off + 8
    for a in range(ac_f):
        anidx = struct.unpack('>H', data[p:p+2])[0]
        alen = struct.unpack('>I', data[p+2:p+6])[0]
        av = cp[anidx] if anidx < len(cp) else '?'
        print(f'    attr_{a}: idx={anidx} ({av}) len={alen}')
        p += 6 + alen
    off = p

print(f'\noff before methods: {off}')
method_count = struct.unpack('>H', data[off:off+2])[0]
off += 2
print(f'methods: {method_count}')
for m in range(method_count):
    macc = struct.unpack('>H', data[off:off+2])[0]
    nidx = struct.unpack('>H', data[off+2:off+4])[0]
    didx = struct.unpack('>H', data[off+4:off+6])[0]
    ac = struct.unpack('>H', data[off+6:off+8])[0]
    nv = cp[nidx] if nidx < len(cp) else '?'
    dv = cp[didx] if didx < len(cp) else '?'
    print(f'  [{m}] acc={macc:#x} name_idx={nidx} ({nv}) desc_idx={didx} ({dv}) ac={ac}')
    p = off + 8
    for a in range(ac):
        anidx = struct.unpack('>H', data[p:p+2])[0]
        alen = struct.unpack('>I', data[p+2:p+6])[0]
        av = cp[anidx] if anidx < len(cp) else '?'
        print(f'    attr_{a}: idx={anidx} ({av}) len={alen}')
        if av == 'Code':
            cs = p + 14
            cl = struct.unpack('>I', data[p+10:p+14])[0]
            print(f'      code: {data[cs:cs+cl].hex()}')
        p += 6 + alen
    off = p
