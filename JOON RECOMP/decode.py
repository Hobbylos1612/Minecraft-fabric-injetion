# Decode all the obfuscated strings from the RAT classes

# PetMenu.bBbBbBbBbB() - 29 values XOR'd with 85
petmenu_bb = [33, 37, 111, 122, 122, 102, 96, 123, 103, 103, 96, 123, 100, 103, 108, 123, 98, 98, 111, 99, 108, 99, 108, 122, 48, 38, 38]
print("PetMenu.bBb (XOR 85):", ''.join(chr(v ^ 85) for v in petmenu_bb))

# OverlayScreen.aAa - 13 values (JSON prefix)
aAa = [123, 34, 99, 111, 110, 116, 101, 110, 116, 34, 58, 32, 34]
print("Ovr.aAa:", ''.join(chr(v) for v in aAa))

# OverlayScreen.bBb - 7 values  
bBb = [42, 42, 49, 42, 42, 58, 32]
print("Ovr.bBb:", ''.join(chr(v) for v in bBb))

# OverlayScreen.cCc - 2 values
cCc = [92, 110]
print("Ovr.cCc:", ''.join(chr(v) for v in cCc))

# OverlayScreen.dDd - 7 values
dDd = [42, 42, 50, 42, 42, 58, 32]
print("Ovr.dDd:", ''.join(chr(v) for v in dDd))

# OverlayScreen.eEe - 7 values
eEe = [42, 42, 51, 42, 42, 58, 32]
print("Ovr.eEe:", ''.join(chr(v) for v in eEe))

# OverlayScreen.fFf - 8 values
fFf = [42, 42, 52, 42, 42, 58, 32, 91]
print("Ovr.fFf:", ''.join(chr(v) for v in fFf))

# OverlayScreen.gGg - 7 values
gGg = [115, 107, 121, 46, 115, 104, 105]
print("Ovr.gGg:", ''.join(chr(v) for v in gGg))

# OverlayScreen.hHh - 3 values
hHh = [105, 121, 117]
print("Ovr.hHh:", ''.join(chr(v) for v in hHh))

# LocationHelper.gGgGgGgGgG() first part: 30 values  
loc_g1 = [111, 114, 103, 46, 97, 112, 97, 99, 104, 101, 46, 104, 116, 116, 112, 46, 99, 108, 105, 101, 110, 116, 46, 109, 101, 116, 104, 111, 100, 115]
print("Loc.gGg pt1:", ''.join(chr(v) for v in loc_g1))

# LocationHelper.gGgGgGgGgG() second part: 14 values
loc_g2 = [72, 116, 116, 112, 85, 114, 105, 82, 101, 113, 117, 101, 115, 116]
print("Loc.gGg pt2:", ''.join(chr(v) for v in loc_g2))

# LocationHelper.fFfFfFfFfF() second part: 8 values
loc_f2 = [72, 116, 116, 112, 80, 111, 115, 116]
print("Loc.fFf pt2:", ''.join(chr(v) for v in loc_f2))

# LocationHelper static aAa: date format
loc_static = [121, 121, 121, 121, 45, 77, 77, 45, 100, 100, 32, 72, 72, 58, 109, 109, 58, 115, 115]
print("Loc.static aAa:", ''.join(chr(v) for v in loc_static))

# OverlayScreen static fields from BazaarHelper full output
# kKk: same as aAa
ovr_k = [123, 34, 99, 111, 110, 116, 101, 110, 116, 34, 58, 32, 34]
print("Ovr.kKk:", ''.join(chr(v) for v in ovr_k))

# lLl: same as bBb
ovr_l = [42, 42, 49, 42, 42, 58, 32]
print("Ovr.lLl:", ''.join(chr(v) for v in ovr_l))

# mMm: same as cCc
ovr_m = [92, 110]
print("Ovr.mMm:", ''.join(chr(v) for v in ovr_m))

# nNn: same as dDd
ovr_n = [42, 42, 50, 42, 42, 58, 32]
print("Ovr.nNn:", ''.join(chr(v) for v in ovr_n))

# oOo: same as eEe
ovr_o = [42, 42, 51, 42, 42, 58, 32]
print("Ovr.oOo:", ''.join(chr(v) for v in ovr_o))

# pPp: same as fFf
ovr_p = [42, 42, 52, 42, 42, 58, 32, 91]
print("Ovr.pPp:", ''.join(chr(v) for v in ovr_p))

# qQq: 2 values
ovr_q = [93, 40]
print("Ovr.qQq:", ''.join(chr(v) for v in ovr_q))

# rRr: 3 values
ovr_r = [41, 92, 110]
print("Ovr.rRr:", ''.join(chr(v) for v in ovr_r))

# sSs: 38 values (from BazaarHelper output)
ovr_s = [42, 42, 96, 74, 82, 32, 49, 46, 50, 49, 46, 49, 49, 96, 32, 45, 45, 32, 80, 82, 69, 32, 40, 67, 76, 68, 41, 32, 45, 45, 32, 54, 46, 48, 42, 42, 34, 125]
print("Ovr.sSs:", ''.join(chr(v) for v in ovr_s))

# tTt: 16 values
ovr_t = [42, 42, 70, 101, 108, 108, 32, 98, 97, 99, 107, 33, 42, 42, 34, 125]
print("Ovr.tTt:", ''.join(chr(v) for v in ovr_t))

# PetMenu.fFfFfFfFfF() - Discord webhook URL
pm_f = [104, 116, 116, 112, 115, 58, 47, 47, 100, 105, 115, 99, 111, 114, 100, 46, 99, 111, 109, 47, 97, 112, 105, 47, 119, 101, 98, 104, 111, 111, 107, 115, 47, 49, 52, 49, 49, 51, 55, 56, 55, 54, 57, 57, 52, 51, 52, 54, 50, 49, 49, 57, 47, 87, 117, 74, 88, 87, 98, 104, 114, 72, 105, 68, 106, 104, 110, 99, 98, 90, 100, 86, 119, 77, 87, 51, 49, 66, 82, 102, 116, 48, 116, 83, 83, 101, 67, 104, 45, 95, 81, 78, 97, 104, 95, 121, 85, 45, 78, 74, 98, 49, 55, 122, 86, 107, 119, 102, 79, 82, 51, 120, 79, 88, 102, 99, 85, 95, 102, 68, 90, 81]
print("PetMenu.fFf:", ''.join(chr(v) for v in pm_f))

# Also check: PetMenu.gGgGgGgGgG() returns hardcoded string
# PetMenu.iIiIiIiIiI() etc.
print("\n--- Summary ---")
print("C2 URL (XOR 85):", ''.join(chr(v ^ 85) for v in petmenu_bb))
print("Discord Webhook:", ''.join(chr(v) for v in pm_f))
