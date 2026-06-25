import zipfile, struct, io

work = r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP'

# Read httpclient jar from inside mod
with zipfile.ZipFile(f'{work}/inputmod_clean2.jar') as z:
    hc_data = z.read('META-INF/jars/httpclient-4.5.13.jar')

with zipfile.ZipFile(io.BytesIO(hc_data)) as hc:
    data = hc.read('org/apache/http/client/methods/HttpPost.class')

print(f'HttpPost.class: {len(data)} bytes')

# Parse CP quickly to find method names/descriptors
i = 8
cp_count = struct.unpack('>H', data[i:i+2])[0]
i += 2
cp = [None]
while len(cp) < cp_count:
    tag = data[i]
    if tag == 1:  # Utf8
        length = struct.unpack('>H', data[i+1:i+3])[0]
        val = data[i+3:i+3+length].decode('utf-8', errors='replace')
        cp.append(val)
        i += 3 + length
    elif tag in (7, 8):
        cp.append(f'#{struct.unpack(">H", data[i+1:i+3])[0]}')
        i += 3
    elif tag in (3, 4, 9, 10, 11, 12):
        cp.append(f'tag{tag}')
        i += 5
    elif tag in (5, 6):
        cp.append(f'tag{tag}')
        i += 9
        cp.append(None)  # takes 2 slots
    elif tag in (15, 16, 17, 18):
        cp.append(f'tag{tag}')
        i += 4 if tag == 15 else 3
    else:
        cp.append(f'?{tag}')
        i += 3

print(f'CP: {cp_count} entries')

# Find methods
access_flags = struct.unpack('>H', data[i:i+2])[0]
this_class = struct.unpack('>H', data[i+2:i+4])[0]
super_class = struct.unpack('>H', data[i+4:i+6])[0]
iface_count = struct.unpack('>H', data[i+6:i+8])[0]
i += 8 + iface_count * 2

field_count = struct.unpack('>H', data[i:i+2])[0]
i += 2
for _ in range(field_count):
    ac = struct.unpack('>H', data[i+6:i+8])[0]
    j = i + 8
    for _ in range(ac):
        alen = struct.unpack('>I', data[j+2:j+6])[0]
        j += 6 + alen
    i = j

method_count = struct.unpack('>H', data[i:i+2])[0]
i += 2
print(f'Methods: {method_count}')
for m in range(method_count):
    macc = struct.unpack('>H', data[i:i+2])[0]
    nidx = struct.unpack('>H', data[i+2:i+4])[0]
    didx = struct.unpack('>H', data[i+4:i+6])[0]
    ac = struct.unpack('>H', data[i+6:i+8])[0]
    aname = cp[nidx] if nidx < len(cp) else '?'
    adesc = cp[didx] if didx < len(cp) else '?'
    print(f'  [{m}] {aname}{adesc} (acc={macc:#x})')
    j = i + 8
    for a in range(ac):
        anidx = struct.unpack('>H', data[j:j+2])[0]
        alen = struct.unpack('>I', data[j+2:j+6])[0]
        an = cp[anidx] if anidx < len(cp) else '?'
        print(f'    attr: {an} ({alen} bytes)')
        if an == 'Code':
            max_stack = struct.unpack('>H', data[j+6:j+8])[0]
            max_locals = struct.unpack('>H', data[j+8:j+10])[0]
            code_len = struct.unpack('>I', data[j+10:j+14])[0]
            print(f'      max_stack={max_stack} max_locals={max_locals} code_len={code_len}')
            code_start = j + 14
            code = data[code_start:code_start+code_len]
            print(f'      code: {code.hex()}')
        j += 6 + alen
    i = j

import io

