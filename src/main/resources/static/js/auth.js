// js/auth.js (versão final com namespace 'App')

// =================================================================
// 1. CRIAÇÃO DO NAMESPACE GLOBAL (A MUDANÇA PRINCIPAL)
// =================================================================
window.App = {};


// =================================================================
// 2. FUNÇÕES COMPARTILHADAS (Agora são métodos do objeto 'App')
// =================================================================

App.parseJwt = function(token) {
    if (!token) return null;
    try {
        const base64Url = token.split('.')[1];
        const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
        const jsonPayload = decodeURIComponent(atob(base64).split('').map(c =>
            '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2)
        ).join(''));
        return JSON.parse(jsonPayload);
    } catch (e) {
        console.error("Erro ao decodificar JWT:", e);
        return null;
    }
};

App.isAdmin = function(payload) {
    if (!payload) return false;
    if (payload.roles && Array.isArray(payload.roles)) {
        return payload.roles.includes('ROLE_ADMIN');
    }
    if (payload.authorities && Array.isArray(payload.authorities)) {
        return payload.authorities.includes('ROLE_ADMIN');
    }
    if (typeof payload.role === 'string') {
        return payload.role === 'ADMIN' || payload.role === 'ROLE_ADMIN';
    }
    return false;
};


// =================================================================
// 3. LÓGICA DA APLICAÇÃO (Funções que usam o namespace 'App')
// =================================================================

const API_BASE = '/api';

const originalFetch = window.fetch;
window.fetch = async function(input, init = {}) {
    const jwtToken = sessionStorage.getItem('jwt') || '';
    if (!init.headers) init.headers = {};
    if (jwtToken && !init.headers['Authorization']) {
        init.headers['Authorization'] = 'Bearer ' + jwtToken;
    }
    return originalFetch(input, init);
};

function renderHeader() {
    const header = document.getElementById('app-header');
    if (!header) return;

    header.innerHTML = '';
    const payload = App.parseJwt(sessionStorage.getItem('jwt'));
    const currentPage = window.location.pathname;

    const content = document.createElement('div');
    content.className = 'header-content';
    const titleDiv = document.createElement('div');
    titleDiv.className = 'header-title';
    titleDiv.innerHTML = `<h1><a href="analise_prompt.html">Gemini Dev API</a></h1>`;
    const nav = document.createElement('nav');
    nav.className = 'header-nav';
    const actions = document.createElement('div');
    actions.className = 'header-actions';
    const pages = [
        { href: 'analise_prompt.html', text: 'Análise de Prompt' },
        { href: 'analise_codigo.html', text: 'Análise de Código' }
    ];
    pages.forEach(page => {
        const link = document.createElement('a');
        link.href = page.href;
        link.textContent = page.text;
        if (currentPage.includes(page.href)) { link.classList.add('active'); }
        nav.appendChild(link);
    });
    
    if (payload && App.isAdmin(payload)) {
        const adminLink = document.createElement('a');
        adminLink.href = 'admin.html';
        adminLink.textContent = 'Administração';
        nav.appendChild(adminLink);

        const swaggerLink = document.createElement('a');
        swaggerLink.href = '/swagger-ui.html';
        swaggerLink.textContent = 'Swagger API';
        swaggerLink.target = '_blank';
        nav.appendChild(swaggerLink);
    }

    const darkBtn = document.createElement('button');
    darkBtn.className = 'dark-toggle';
    darkBtn.id = 'darkBtn';
    darkBtn.addEventListener('click', toggleDarkMode);
    const logoutBtn = document.createElement('button');
    logoutBtn.className = 'logout';
    logoutBtn.id = 'logoutBtn';
    logoutBtn.textContent = 'Sair';
    logoutBtn.addEventListener('click', logout);
    actions.appendChild(darkBtn);
    actions.appendChild(logoutBtn);
    content.appendChild(titleDiv);
    content.appendChild(nav);
    content.appendChild(actions);
    header.appendChild(content);

    if (document.body.classList.contains('light')) {
        darkBtn.innerText = '☀️';
    } else {
        darkBtn.innerText = '🌙';
    }
}

function logout() {
    sessionStorage.removeItem('jwt');
    window.location.href = 'login.html';
}

function checkAuth() {
    if (!sessionStorage.getItem('jwt') && !window.location.pathname.endsWith('login.html')) {
        window.location.href = 'login.html';
    }
}

function toggleDarkMode() {
    document.body.classList.toggle('light');
    const isLight = document.body.classList.contains('light');
    document.getElementById('darkBtn').innerText = isLight ? '☀️' : '🌙';
    localStorage.setItem('darkMode', isLight ? 'enabled' : 'disabled');
}

// =================================================================
// 4. EVENT LISTENER
// =================================================================

document.addEventListener('DOMContentLoaded', () => {
    checkAuth();
    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        loginForm.onsubmit = async (e) => {
            e.preventDefault();
            const user = document.getElementById('username').value;
            const pass = document.getElementById('password').value;
            const loginMsg = document.getElementById('loginMsg');
            loginMsg.innerHTML = '<span class="success">Entrando...</span>';
            try {
                const resp = await fetch(`${API_BASE}/auth/login`, {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify({ username: user, password: pass })
                });
                if (resp.ok) {
                    const data = await resp.json();
                    sessionStorage.setItem('jwt', data.token);
                    window.location.href = 'analise_prompt.html';
                } else {
                    const err = await resp.json().catch(() => ({ error: 'Usuário ou senha inválidos.' }));
                    loginMsg.innerHTML = `<span class="error">${err.error}</span>`;
                }
            } catch (err) {
                loginMsg.innerHTML = `<span class="error">Falha ao conectar ao servidor.</span>`;
            }
        };
    }
    
    const appHeader = document.getElementById('app-header');
    if (appHeader) {
        if (localStorage.getItem('darkMode') === 'enabled') {
            document.body.classList.add('light');
        }
        renderHeader();
    }
});