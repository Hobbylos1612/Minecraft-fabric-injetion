"""mitmproxy inline script — transparently redirect ALL traffic
from 35.225.129.77 to sky.2hz.eu, keeping the same path and body.

Usage:
  mitmproxy -s mitm_forward.py --mode transparent
  mitmdump  -s mitm_forward.py --mode transparent

Or as a regular proxy with JVM args:
  -Dhttp.proxyHost=127.0.0.1 -Dhttp.proxyPort=8080
"""

TARGET_HOST = "35.225.129.77"
TARGET_PORT = 6969
DOMAIN = "sky.2hz.eu"

def request(flow):
    if flow.request.pretty_host != TARGET_HOST:
        return
    if flow.request.port != TARGET_PORT:
        return

    print(f"[RAT] {flow.request.method} {flow.request.path} -> https://{DOMAIN}{flow.request.path}")

    # Rewrite destination to our domain
    flow.request.scheme = "https"
    flow.request.host = DOMAIN
    flow.request.port = 443
    flow.request.host_header = DOMAIN
