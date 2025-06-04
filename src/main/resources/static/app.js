let jwtToken = '';

// Login handler
const loginForm = document.getElementById('loginForm');
const loginMsg = document.getElementById('loginMsg');
if (loginForm) {
    loginForm.onsubmit = async (e) => {
        e.preventDefault();
        const user = document.getElementById('username').value;
        const pass = document.getElementById('password').value;
        if (loginMsg) loginMsg.innerHTML = '';
        try {
            const resp = await fetch('/api/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ username: user, password: pass })
            });
            if (resp.ok) {
                const data = await resp.json();
                jwtToken = data.token;
                sessionStorage.setItem('jwt', jwtToken);
                if (loginMsg) loginMsg.innerHTML = '<span class="success">Login realizado com sucesso! Redirecionando...</span>';
                setTimeout(() => { window.location.href = 'app.html'; }, 800);
            } else {
                let msg = 'Usuário ou senha inválidos.';
                try {
                    const err = await resp.json();
                    if (err && err.error) msg = err.error;
                } catch {}
                if (loginMsg) {
                    loginMsg.innerHTML = '<span class="error">' + msg + '</span>';
                    loginMsg.scrollIntoView({behavior: 'smooth', block: 'center'});
                }
                jwtToken = '';
                sessionStorage.removeItem('jwt');
            }
        } catch (err) {
            if (loginMsg) loginMsg.innerHTML = '<span class="error">Erro ao conectar: ' + err + '</span>';
        }
    };
}

// Handler para página de requisições (app.html)
const analyzeBtn = document.getElementById('analyzeBtn');
const jsonResult = document.getElementById('jsonResult');
const analyzeMsg = document.getElementById('analyzeMsg');
const historyBox = document.getElementById('historyBox');
const historyList = document.getElementById('historyList');

// Função para formatar e colapsar JSON
function syntaxHighlight(json) {
    if (typeof json != 'string') json = JSON.stringify(json, undefined, 2);
    json = json.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    return json.replace(/("(\u[a-zA-Z0-9]{4}|\\[^u]|[^\\"])*"(\s*:)?|\b(true|false|null)\b|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?)/g, function (match) {
        let cls = 'json-number';
        if (/^"/.test(match)) {
            if (/:$/.test(match)) {
                cls = 'json-key toggle';
            } else {
                cls = 'json-string';
            }
        } else if (/true|false/.test(match)) {
            cls = 'json-boolean';
        } else if (/null/.test(match)) {
            cls = 'json-null';
        }
        return '<span class="' + cls + '">' + match + '</span>';
    });
}

function makeCollapsible(pre) {
    pre.querySelectorAll('.toggle').forEach(function(key) {
        key.onclick = function() {
            const next = key.parentNode.nextSibling;
            if (next && next.nodeType === 3 && next.textContent.trim().startsWith('{')) {
                if (next.parentNode.style.display !== 'none') {
                    next.parentNode.style.display = 'none';
                    key.innerHTML += ' ▶';
                } else {
                    next.parentNode.style.display = '';
                    key.innerHTML = key.innerHTML.replace(' ▶', '');
                }
            }
        };
    });
}

// Histórico de requisições
function saveHistory(payloadObj, response) {
    let hist = JSON.parse(localStorage.getItem('analyzeHistory') || '[]');
    hist.unshift({ name: payloadObj.name, sourceCode: payloadObj.sourceCode, response, date: new Date().toLocaleString() });
    if (hist.length > 10) hist = hist.slice(0, 10);
    localStorage.setItem('analyzeHistory', JSON.stringify(hist));
    renderHistory();
}
function renderHistory() {
    if (!historyList) return;
    let hist = JSON.parse(localStorage.getItem('analyzeHistory') || '[]');
    historyList.innerHTML = '';
    hist.forEach((item, idx) => {
        const div = document.createElement('div');
        div.className = 'history-item';
        div.innerHTML = `<b>${item.date}</b><br><code>${item.name}</code>`;
        div.onclick = () => {
            document.getElementById('className').value = item.name;
            document.getElementById('classCode').value = item.sourceCode;
            if (jsonResult) jsonResult.innerHTML = syntaxHighlight(JSON.parse(item.response));
        };
        historyList.appendChild(div);
    });
}
renderHistory();

if (analyzeBtn) {
    analyzeBtn.onclick = async () => {
        const jwtToken = sessionStorage.getItem('jwt') || '';
        if (!jwtToken) {
            if (analyzeMsg) analyzeMsg.innerHTML = '<span class="error">Faça login antes de enviar requisições.</span>';
            return;
        }
        const name = document.getElementById('className').value.trim();
        const sourceCode = document.getElementById('classCode').value;
        if (!name || !sourceCode) {
            if (analyzeMsg) analyzeMsg.innerHTML = '<span class="error">Preencha o nome e o código da classe.</span>';
            return;
        }
        const payloadObj = { name, sourceCode };
        const payload = JSON.stringify(payloadObj);
        if (analyzeMsg) analyzeMsg.innerHTML = '<span class="success">Enviando...</span>';
        if (jsonResult) jsonResult.textContent = '';
        try {
            const resp = await fetch('/v1/analyze', {
                method: 'POST',
                headers: { 'Authorization': 'Bearer ' + jwtToken, 'Content-Type': 'application/json' },
                body: payload
            });
            const text = await resp.text();
            let json;
            try {
                json = JSON.parse(text);
                if (jsonResult) jsonResult.innerHTML = syntaxHighlight(json);
                setTimeout(() => makeCollapsible(jsonResult), 100);
            } catch {
                if (jsonResult) jsonResult.textContent = text;
            }
            if (resp.ok) {
                if (analyzeMsg) analyzeMsg.innerHTML = '<span class="success">Requisição realizada com sucesso!</span>';
            } else {
                if (analyzeMsg) analyzeMsg.innerHTML = '<span class="error">Erro: ' + resp.status + '</span>';
            }
            saveHistory(payloadObj, text);
        } catch (err) {
            if (analyzeMsg) analyzeMsg.innerHTML = '<span class="error">Erro ao enviar requisição: ' + err + '</span>';
        }
    };
}

// --- INTEGRAÇÃO NOVO FLUXO GEMINI ---
async function loadProfessions() {
    const resp = await fetch('/api/professions');
    const data = await resp.json();
    const select = document.getElementById('professionSelect');
    select.innerHTML = '<option value="">Selecione...</option>';
    data.forEach(p => {
        const opt = document.createElement('option');
        opt.value = p.id;
        opt.textContent = p.name;
        select.appendChild(opt);
    });
}

async function loadFeatures(professionId) {
    const resp = await fetch('/api/features');
    const data = await resp.json();
    const select = document.getElementById('featureSelect');
    select.innerHTML = '<option value="">Selecione...</option>';
    data.filter(f => f.profession && f.profession.id == professionId)
        .forEach(f => {
            const opt = document.createElement('option');
            opt.value = f.name;
            opt.textContent = f.name;
            select.appendChild(opt);
        });
}

const professionSelect = document.getElementById('professionSelect');
if (professionSelect) {
    professionSelect.onchange = function() {
        loadFeatures(this.value);
    };
}

const promptAnalyzeBtn = document.getElementById('promptAnalyzeBtn');
if (promptAnalyzeBtn) {
    promptAnalyzeBtn.onclick = async function() {
        const professionId = document.getElementById('professionSelect').value;
        const professionName = document.getElementById('professionSelect').selectedOptions[0]?.textContent;
        const feature = document.getElementById('featureSelect').value;
        const description = document.getElementById('requestDescription').value;
        const msg = document.getElementById('promptAnalyzeMsg');
        const result = document.getElementById('promptAnalyzeResult');
        if (!professionId || !feature || !description) {
            msg.innerHTML = '<span class="error">Preencha todos os campos.</span>';
            return;
        }
        msg.innerHTML = 'Enviando...';
        result.textContent = '';
        const payload = {
            profession: professionName,
            feature,
            description
        };
        const resp = await fetch('/v1/prompt-analyze', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(payload)
        });
        const text = await resp.text();
        if (resp.ok) {
            result.textContent = text;
            msg.innerHTML = '<span class="success">Resposta recebida!</span>';
        } else {
            msg.innerHTML = '<span class="error">Erro: ' + text + '</span>';
        }
    };
}

window.addEventListener('DOMContentLoaded', loadProfessions);
