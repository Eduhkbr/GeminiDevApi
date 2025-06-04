// admin.js - CRUD Usuários, Profissões, Funcionalidades

// Controle de acesso: só ADMIN
function parseJwt(token) {
    if (!token) return null;
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(function(c) {
            return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        }).join(''));
        return JSON.parse(jsonPayload);
    } catch { return null; }
}

const jwt = sessionStorage.getItem('jwt');
const payload = parseJwt(jwt);
if (!payload || !payload.roles || !payload.roles.includes('ADMIN')) {
    alert('Acesso restrito a administradores.');
    window.location.href = '/login.html';
}

// Tabs
const tabBtns = document.querySelectorAll('.tab-btn');
const tabContents = document.querySelectorAll('.tab-content');
tabBtns.forEach(btn => btn.onclick = function() {
    tabBtns.forEach(b => b.classList.remove('active'));
    tabContents.forEach(tc => tc.classList.remove('active'));
    btn.classList.add('active');
    document.getElementById('tab-' + btn.dataset.tab).classList.add('active');
});

// --- Usuários ---
async function loadUsers() {
    const resp = await fetch('/api/users');
    const data = await resp.json();
    const tbody = document.querySelector('#userTable tbody');
    tbody.innerHTML = '';
    data.forEach(u => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${u.id}</td><td>${u.username}</td><td>${u.role}</td><td class='actions'>
            <button onclick="deleteUser(${u.id})">Remover</button></td>`;
        tbody.appendChild(tr);
    });
}
async function addUser() {
    const username = document.getElementById('newUsername').value;
    const password = document.getElementById('newPassword').value;
    const role = document.getElementById('newUserRole').value;
    const msg = document.getElementById('userMsg');
    if (!username || !password) { msg.textContent = 'Preencha usuário e senha.'; msg.className = 'error'; return; }
    const resp = await fetch('/api/users', {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username, password, role })
    });
    if (resp.ok) { msg.textContent = 'Usuário adicionado!'; msg.className = 'success'; loadUsers(); }
    else { msg.textContent = 'Erro ao adicionar.'; msg.className = 'error'; }
}
async function deleteUser(id) {
    if (!confirm('Remover usuário?')) return;
    const resp = await fetch(`/api/users/${id}`, { method: 'DELETE' });
    if (resp.ok) loadUsers();
}
document.getElementById('addUserBtn').onclick = addUser;

// --- Profissões ---
async function loadProfs() {
    const resp = await fetch('/api/professions');
    const data = await resp.json();
    const tbody = document.querySelector('#profTable tbody');
    tbody.innerHTML = '';
    data.forEach(p => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${p.id}</td><td>${p.name}</td><td class='actions'>
            <button onclick="deleteProf(${p.id})">Remover</button></td>`;
        tbody.appendChild(tr);
    });
    // Atualiza select de profissões para funcionalidades
    const select = document.getElementById('newFeatProf');
    select.innerHTML = '';
    data.forEach(p => {
        const opt = document.createElement('option');
        opt.value = p.id;
        opt.textContent = p.name;
        select.appendChild(opt);
    });
}
async function addProf() {
    const name = document.getElementById('newProfName').value;
    const msg = document.getElementById('profMsg');
    if (!name) { msg.textContent = 'Preencha o nome.'; msg.className = 'error'; return; }
    const resp = await fetch('/api/professions', {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name })
    });
    if (resp.ok) { msg.textContent = 'Profissão adicionada!'; msg.className = 'success'; loadProfs(); }
    else { msg.textContent = 'Erro ao adicionar.'; msg.className = 'error'; }
}
async function deleteProf(id) {
    if (!confirm('Remover profissão?')) return;
    const resp = await fetch(`/api/professions/${id}`, { method: 'DELETE' });
    if (resp.ok) loadProfs();
}
document.getElementById('addProfBtn').onclick = addProf;

// --- Funcionalidades ---
async function loadFeats() {
    const resp = await fetch('/api/features');
    const data = await resp.json();
    const tbody = document.querySelector('#featTable tbody');
    tbody.innerHTML = '';
    data.forEach(f => {
        const tr = document.createElement('tr');
        tr.innerHTML = `<td>${f.id}</td><td>${f.name}</td><td>${f.profession ? f.profession.name : ''}</td><td class='actions'>
            <button onclick="deleteFeat(${f.id})">Remover</button></td>`;
        tbody.appendChild(tr);
    });
}
async function addFeat() {
    const name = document.getElementById('newFeatName').value;
    const profId = document.getElementById('newFeatProf').value;
    const msg = document.getElementById('featMsg');
    if (!name || !profId) { msg.textContent = 'Preencha todos os campos.'; msg.className = 'error'; return; }
    const resp = await fetch('/api/features', {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name, profession: { id: profId } })
    });
    if (resp.ok) { msg.textContent = 'Funcionalidade adicionada!'; msg.className = 'success'; loadFeats(); }
    else { msg.textContent = 'Erro ao adicionar.'; msg.className = 'error'; }
}
async function deleteFeat(id) {
    if (!confirm('Remover funcionalidade?')) return;
    const resp = await fetch(`/api/features/${id}`, { method: 'DELETE' });
    if (resp.ok) loadFeats();
}
document.getElementById('addFeatBtn').onclick = addFeat;

// Inicialização
window.addEventListener('DOMContentLoaded', () => {
    loadUsers();
    loadProfs();
    loadFeats();
});
