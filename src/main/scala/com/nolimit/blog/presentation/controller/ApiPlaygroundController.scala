package com.nolimit.blog.presentation.controller

import cats.effect.IO
import org.http4s.{Header, HttpRoutes}
import org.http4s.dsl.io._
import org.typelevel.ci.CIString

class ApiPlaygroundController {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root =>
      Ok(
        ApiPlaygroundPage.html,
        Header.Raw(CIString("Content-Type"), "text/html; charset=utf-8")
      )
  }
}

object ApiPlaygroundPage {
  val html: String =
    """<!doctype html>
<html lang="id">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Scala Blog API Playground</title>
  <style>
    :root { color-scheme: dark; font-family: Inter, system-ui, sans-serif; }
    body { margin: 0; background: #101827; color: #e5e7eb; }
    header { padding: 28px max(24px, calc((100vw - 1120px) / 2)); background: #172554; border-bottom: 1px solid #334155; }
    h1 { margin: 0 0 8px; font-size: 1.8rem; } p { margin: 0; color: #cbd5e1; }
    main { max-width: 1120px; margin: 0 auto; padding: 24px; display: grid; grid-template-columns: 1fr 1fr; gap: 20px; }
    section { background: #172033; border: 1px solid #334155; border-radius: 12px; padding: 18px; }
    .wide { grid-column: 1 / -1; } h2 { margin: 0 0 14px; font-size: 1.1rem; }
    .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 10px; } label { display: grid; gap: 6px; color: #cbd5e1; font-size: .88rem; }
    input, textarea { box-sizing: border-box; width: 100%; background: #0f172a; color: #f8fafc; border: 1px solid #475569; border-radius: 7px; padding: 10px; font: inherit; }
    textarea { min-height: 74px; resize: vertical; } button { margin-top: 12px; border: 0; border-radius: 7px; padding: 10px 14px; background: #38bdf8; color: #082f49; font-weight: 700; cursor: pointer; }
    button:hover { background: #7dd3fc; } .secondary { background: #cbd5e1; } pre { overflow: auto; min-height: 140px; margin: 0; padding: 16px; border-radius: 9px; background: #020617; color: #a7f3d0; white-space: pre-wrap; }
    .hint { margin-top: 10px; font-size: .85rem; color: #94a3b8; } code { color: #bae6fd; } @media (max-width: 760px) { main { grid-template-columns: 1fr; } .wide { grid-column: auto; } .grid { grid-template-columns: 1fr; } }
  </style>
</head>
<body>
  <header>
    <h1>Scala Blog API Playground</h1>
    <p>Uji endpoint API langsung dari browser. Login akan menyimpan token JWT di halaman ini saja.</p>
  </header>
  <main>
    <section>
      <h2>1. Register</h2>
      <div class="grid">
        <label>Nama<input id="register-name" value="Test User"></label>
        <label>Email<input id="register-email" type="email" placeholder="test@example.com"></label>
      </div>
      <label>Password<input id="register-password" type="password" value="password123"></label>
      <button onclick="register()">POST /register</button>
    </section>
    <section>
      <h2>2. Login</h2>
      <label>Email<input id="login-email" type="email" placeholder="email hasil register"></label>
      <label>Password<input id="login-password" type="password" value="password123"></label>
      <button onclick="login()">POST /login</button>
      <p class="hint">Token hasil login otomatis diisi di bawah.</p>
    </section>
    <section class="wide">
      <h2>JWT Token</h2>
      <textarea id="token" placeholder="Login terlebih dahulu atau tempel JWT token di sini"></textarea>
    </section>
    <section>
      <h2>3. Lihat Post</h2>
      <button onclick="send('GET', '/posts')">GET /posts</button>
      <label>Post ID<input id="get-id" placeholder="UUID post"></label>
      <button class="secondary" onclick="getPost()">GET /posts/{id}</button>
    </section>
    <section>
      <h2>4. Buat Post</h2>
      <label>Content<textarea id="create-content" placeholder="Tulis content post"></textarea></label>
      <button onclick="createPost()">POST /posts</button>
    </section>
    <section>
      <h2>5. Ubah Post</h2>
      <label>Post ID<input id="update-id" placeholder="UUID post"></label>
      <label>Content<textarea id="update-content" placeholder="Content baru"></textarea></label>
      <button onclick="updatePost()">PUT /posts/{id}</button>
    </section>
    <section>
      <h2>6. Hapus Post</h2>
      <label>Post ID<input id="delete-id" placeholder="UUID post"></label>
      <button onclick="deletePost()">DELETE /posts/{id}</button>
    </section>
    <section class="wide">
      <h2>Response</h2>
      <pre id="output">Klik salah satu tombol untuk mengirim request.</pre>
    </section>
  </main>
  <script>
    const value = id => document.getElementById(id).value.trim();
    const output = document.getElementById('output');
    async function send(method, path, body, requiresToken = false) {
      const headers = {};
      if (body) headers['Content-Type'] = 'application/json';
      if (requiresToken) headers.Authorization = `Bearer ${value('token')}`;
      try {
        const response = await fetch(path, { method, headers, body: body ? JSON.stringify(body) : undefined });
        const text = await response.text();
        let display = text;
        try { display = JSON.stringify(JSON.parse(text), null, 2); } catch (_) {}
        output.textContent = `${method} ${path} -> ${response.status} ${response.statusText}\n\n${display}`;
        return { response, text };
      } catch (error) {
        output.textContent = `Request gagal: ${error.message}`;
      }
    }
    function register() { return send('POST', '/register', { name: value('register-name'), email: value('register-email'), password: value('register-password') }); }
    async function login() {
      const result = await send('POST', '/login', { email: value('login-email'), password: value('login-password') });
      if (result && result.response.ok) document.getElementById('token').value = JSON.parse(result.text).data.token;
    }
    function getPost() { return send('GET', `/posts/${value('get-id')}`); }
    function createPost() { return send('POST', '/posts', { content: value('create-content') }, true); }
    function updatePost() { return send('PUT', `/posts/${value('update-id')}`, { content: value('update-content') }, true); }
    function deletePost() { return send('DELETE', `/posts/${value('delete-id')}`, undefined, true); }
  </script>
</body>
</html>"""
}
