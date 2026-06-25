import zipfile

with zipfile.ZipFile(r'inputmod_clean2.jar') as z:
    for name in z.namelist():
        if not name.endswith('.class'):
            continue
        data = z.read(name)

        # Check for the original IP in plain text
        ip = b'35.225.129.77'
        if ip in data:
            idx = data.find(ip)
            print(f'PLAIN IP in {name} at offset {idx}')

        # Check for the port 6969 in plain text
        port = b'6969'
        if port in data:
            idx = data.find(port)
            print(f'PLAIN PORT in {name} at offset {idx}')

        # Check for XOR-encoded IP (key 85)
        xor_ip = bytes(b ^ 85 for b in ip)
        if xor_ip in data:
            idx = data.find(xor_ip)
            print(f'XOR IP in {name} at offset {idx}')
            # Show surrounding bytes
            start = max(0, idx - 5)
            end = min(len(data), idx + len(xor_ip) + 5)
            hex_str = ' '.join(f'{b:02x}' for b in data[start:end])
            print(f'  context: {hex_str}')

        # Check for XOR-encoded port (key 85)
        xor_port = bytes(b ^ 85 for b in port)
        if xor_port in data:
            idx = data.find(xor_port)
            print(f'XOR PORT in {name} at offset {idx}')
            start = max(0, idx - 5)
            end = min(len(data), idx + len(xor_port) + 5)
            hex_str = ' '.join(f'{b:02x}' for b in data[start:end])
            print(f'  context: {hex_str}')

print('=== Done searching ===')
