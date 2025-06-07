document.addEventListener('DOMContentLoaded', () => {

    // =================================================================
    // 1. CONTROLE DE ACESSO (Executado primeiro)
    // =================================================================
    const payload = App.parseJwt(sessionStorage.getItem('jwt'));

    // Usando a função centralizada e correta do auth.js!
    if (!App.isAdmin(payload)) {
        alert('Acesso restrito a administradores.');
        window.location.href = 'analise_prompt.html'; 
        return; 
    }
    
    // =================================================================
    // 2. LÓGICA DAS TABS DE NAVEGAÇÃO
    // =================================================================
    const tabBtns = document.querySelectorAll('.tab-btn');
    const tabContents = document.querySelectorAll('.tab-content');
    tabBtns.forEach(btn => {
        btn.onclick = function() {
            tabBtns.forEach(b => b.classList.remove('active'));
            tabContents.forEach(tc => tc.classList.remove('active'));
            btn.classList.add('active');
            document.getElementById('tab-' + btn.dataset.tab).classList.add('active');
        }
    });

    // =================================================================
    // 3. SELETORES E FUNÇÕES GLOBAIS DO PAINEL
    // =================================================================
    // Seletores para Usuários
    const userTableBody = document.querySelector('#userTable tbody');
    const addUserBtn = document.getElementById('addUserBtn');
    const userMsg = document.getElementById('userMsg');

    // Seletores para Profissões
    const profTableBody = document.querySelector('#profTable tbody');
    const addProfBtn = document.getElementById('addProfBtn');
    const profMsg = document.getElementById('profMsg');
    
    // Seletores para Funcionalidades
    const featTableBody = document.querySelector('#featTable tbody');
    const addFeatBtn = document.getElementById('addFeatBtn');
    const featMsg = document.getElementById('featMsg');

    // =================================================================
    // 4. CRUD DE USUÁRIOS
    // =================================================================
    async function loadUsers() {
        try {
            const resp = await fetch('/api/users');
            if (!resp.ok) throw new Error('Falha ao carregar usuários.');
            const data = await resp.json();
            userTableBody.innerHTML = '';
            data.forEach(u => {
                const tr = document.createElement('tr');
                tr.innerHTML = `<td>${u.id}</td><td>${u.username}</td><td>${u.role}</td><td class='actions'>
                    <button class="delete-btn" data-entity="user" data-id="${u.id}">Remover</button></td>`;
                userTableBody.appendChild(tr);
            });
        } catch (error) {
            userMsg.textContent = error.message; userMsg.className = 'error';
        }
    }

async function addUser() {
    console.log("--- DEBUG: Função addUser() iniciada. ---");

    // 1. Pega os elementos do formulário
    const msg = document.getElementById('userMsg');
    const usernameInput = document.getElementById('newUsername');
    const passwordInput = document.getElementById('newPassword');
    const roleSelect = document.getElementById('newUserRole');
    
    // Limpa a mensagem de erro anterior
    if(msg) msg.textContent = '';

    // Verifica se os elementos do formulário existem
    if (!usernameInput || !passwordInput || !roleSelect) {
        console.error("--- DEBUG ERRO: Um ou mais campos do formulário não foram encontrados! Verifique os IDs no seu admin.html (newUsername, newPassword, newUserRole).");
        if(msg) {
            msg.textContent = 'Erro de configuração da página. Contate o suporte.';
            msg.className = 'error';
        }
        return;
    }

    // 2. Pega os valores dos campos
    const username = usernameInput.value;
    const password = passwordInput.value;
    const role = roleSelect.value;
    console.log(`--- DEBUG: Valores do formulário: username='${username}', password='${password}', role='${role}'`);

    // 3. Validação dos dados
    if (!username || !password) {
        console.warn("--- DEBUG AVISO: Validação falhou. Usuário ou senha em branco. A função vai parar aqui.");
        if(msg) {
            msg.textContent = 'Preencha usuário e senha.';
            msg.className = 'error';
        }
        return; // A execução para aqui, a requisição fetch NÃO é feita.
    }
    
    // 4. Se a validação passar, tenta enviar a requisição
    console.log("--- DEBUG: Validação passou. Enviando requisição fetch para /api/users...");
    
    try {
        const resp = await fetch('/api/users', {
            method: 'POST', headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password, role })
        });
        
        console.log("--- DEBUG: Resposta do fetch recebida com status:", resp.status);
        
        if (resp.ok) {
            if(msg) {
                msg.textContent = 'Usuário adicionado!'; msg.className = 'success';
            }
            usernameInput.value = '';
            passwordInput.value = '';
            loadUsers(); // Recarrega a lista de usuários
        } else {
            const err = await resp.text();
            console.error("--- DEBUG ERRO do Backend:", err);
            if(msg) {
                msg.textContent = `Erro ao adicionar: ${err}`; msg.className = 'error';
            }
        }
    } catch (networkError) {
        console.error("--- DEBUG ERRO DE REDE (Failed to fetch):", networkError);
        if(msg) {
            msg.textContent = 'Erro de rede. O backend está no ar?';
            msg.className = 'error';
        }
    }
}
    
// =================================================================
// 5. CRUD DE PROFISSÕES
// =================================================================
async function loadProfs() {
    try {
        const resp = await fetch('/api/professions');
        if (!resp.ok) throw new Error('Falha ao carregar profissões.');
        const data = await resp.json();
        profTableBody.innerHTML = '';
        data.forEach(p => {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td>${p.id}</td><td>${p.name}</td><td class='actions'>
                <button class="delete-btn" data-entity="profession" data-id="${p.id}">Remover</button></td>`;
            profTableBody.appendChild(tr);
        });
        updateProfessionSelect(data);
    } catch (error) {
        profMsg.textContent = error.message; profMsg.className = 'error';
    }
}

async function addProf() {
    const name = document.getElementById('newProfName').value;
    if (!name) { profMsg.textContent = 'Preencha o nome.'; profMsg.className = 'error'; return; }
    const resp = await fetch('/api/professions', {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name })
    });
    if (resp.ok) { profMsg.textContent = 'Profissão adicionada!'; profMsg.className = 'success'; loadProfs(); }
    else { profMsg.textContent = 'Erro ao adicionar.'; profMsg.className = 'error'; }
}

// =================================================================
// 6. CRUD DE FUNCIONALIDADES
// =================================================================
function updateProfessionSelect(professions) {
    const select = document.getElementById('newFeatProf');
    select.innerHTML = '<option value="">Selecione a Profissão...</option>';
    professions.forEach(p => {
        const opt = document.createElement('option');
        opt.value = p.id;
        opt.textContent = p.name;
        select.appendChild(opt);
    });
}

async function loadFeats() {
    try {
        const resp = await fetch('/api/features');
        if (!resp.ok) throw new Error('Falha ao carregar funcionalidades.');
        const data = await resp.json();
        featTableBody.innerHTML = '';
        data.forEach(f => {
            const tr = document.createElement('tr');
            tr.innerHTML = `<td>${f.id}</td><td>${f.name}</td><td>${f.profession ? f.profession.name : ''}</td><td class='actions'>
                <button class="delete-btn" data-entity="feature" data-id="${f.id}">Remover</button></td>`;
            featTableBody.appendChild(tr);
        });
    } catch (error) {
        featMsg.textContent = error.message; featMsg.className = 'error';
    }
}

async function addFeat() {
    const name = document.getElementById('newFeatName').value;
    const profId = document.getElementById('newFeatProf').value;
    if (!name || !profId) { featMsg.textContent = 'Preencha todos os campos.'; featMsg.className = 'error'; return; }
    const resp = await fetch('/api/features', {
        method: 'POST', headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ name: name, professionId: profId }) 
    });
    if (resp.ok) { featMsg.textContent = 'Funcionalidade adicionada!'; featMsg.className = 'success'; loadFeats(); }
    else { featMsg.textContent = 'Erro ao adicionar.'; featMsg.className = 'error'; }
}

// =================================================================
// 7. GERENCIADOR DE EVENTOS (DELEÇÃO CENTRALIZADA)
// =================================================================
async function handleDelete(entity, id) {
    if (!confirm(`Remover ${entity} com ID ${id}?`)) return;
    const resp = await fetch(`/api/${entity}s/${id}`, { method: 'DELETE' }); // 'user' -> 'users', 'profession' -> 'professions'
    
    if (resp.ok) {
        // Recarrega a tabela correta
        if (entity === 'user') loadUsers();
        if (entity === 'profession') loadProfs();
        if (entity === 'feature') loadFeats();
    } else {
        alert(`Erro ao remover ${entity}.`);
    }
}

document.querySelector('.content-wrapper').addEventListener('click', (event) => {
    if (event.target.classList.contains('delete-btn')) {
        const entity = event.target.dataset.entity;
        const id = event.target.dataset.id;
        handleDelete(entity, id);
    }
});

if(addUserBtn) addUserBtn.addEventListener('click', addUser);
if(addProfBtn) addProfBtn.addEventListener('click', addProf);
if(addFeatBtn) addFeatBtn.addEventListener('click', addFeat);


// =================================================================
// 8. INICIALIZAÇÃO DA PÁGINA
// =================================================================
loadUsers();
loadProfs();
loadFeats();

});