"""Verify both patched classes"""
import zipfile, io, struct

WORK = r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP'

with zipfile.ZipFile(f'{WORK}/inputmod_clean2.jar') as z:
    hc = z.read('META-INF/jars/httpclient-4.5.13.jar')

with zipfile.ZipFile(io.BytesIO(hc)) as hcz:
    hp = hcz.read('org/apache/http/client/methods/HttpPost.class')
    hk = hcz.read('org/apache/http/client/methods/HttpPostHook.class')

def parse_cp(data):
    cnt = struct.unpack('>H', data[8:10])[0]
    i = 10; cp = [None]
    while len(cp) < cnt:
        tag = data[i]
        if tag == 1:
            l = struct.unpack('>H', data[i+1:i+3])[0]
            cp.append(data[i+3:i+3+l].decode('utf-8', errors='replace'))
            i += 3 + l
        elif tag in (5,6):
            cp.append('L'); i += 9; cp.append(None)
        elif tag == 15:
            cp.append('M'); i += 4
        elif tag in (16,19,20):
            cp.append('T'); i += 3
        elif tag in (7,8):
            ref = struct.unpack('>H', data[i+1:i+3])[0]
            cp.append(f'C->#{ref}')
            i += 3
        else:
            cp.append('X'); i += 5
    return cp, i

# HttpPost verification
cp, cpe = parse_cp(hp)
off = cpe + 6
if_n = struct.unpack('>H', hp[off:off+2])[0]
off += 2 + if_n * 2
f_n = struct.unpack('>H', hp[off:off+2])[0]
off += 2
for _ in range(f_n):
    ac = struct.unpack('>H', hp[off+6:off+8])[0]
    p = off + 8
    for _ in range(ac):
        al = struct.unpack('>I', hp[p+2:p+6])[0]
        p += 6 + al
    off = p
m_n = struct.unpack('>H', hp[off:off+2])[0]
off += 2
for m in range(m_n):
    ni = struct.unpack('>H', hp[off+2:off+4])[0]
    di = struct.unpack('>H', hp[off+4:off+6])[0]
    ac = struct.unpack('>H', hp[off+6:off+8])[0]
    mn = cp[ni] if ni < len(cp) else '?'
    md = cp[di] if di < len(cp) else '?'
    p = off + 8
    for a in range(ac):
        ai = struct.unpack('>H', hp[p:p+2])[0]
        al = struct.unpack('>I', hp[p+2:p+6])[0]
        an = cp[ai] if ai < len(cp) else '?'
        if an == 'Code' and mn == '<init>' and md == '(Ljava/lang/String;)V':
            cl = struct.unpack('>I', hp[p+10:p+14])[0]
            code = hp[p+14:p+14+cl]
            print(f'HttpPost <init>(String): {cl} bytes')
            print(f'  Code hex: {code.hex()}')
            if b'\xb8' in code:
                print(f'  invokestatic PRESENT at byte {code.index(b"\\xb8")}')
            # Check for 2b (aload_1) before b8
            pos = code.find(b'\x2b\xb8')
            if pos >= 0:
                print(f'  Correct: aload_1 before invokestatic at pos {pos}')
            else:
                pos2 = code.find(b'\xb8')
                before = code[max(0,pos2-2):pos2].hex()
                print(f'  WARNING: bytes before invokestatic: {before}')
        p += 6 + al
    off = p

# HttpPostHook verification
print('\nHttpPostHook class:')
cp2, _ = parse_cp(hk)
for i, v in enumerate(cp2):
    if v and (isinstance(v, str) and ('rewrite' in v or 'Hook' in v or 'String' in v or v.startswith('(L') or v.startswith(')L'))):
        print(f'  CP#{i}: {v}')
