// ===================================
// LÓGICA - ANÁLISE DE PROMPT
// ===================================

/**
 * Salva a requisição no histórico local.
 * Corrigido para salvar os dados corretos do formulário de prompt.
 */
function saveHistory(payload, responseText) {
    let hist = JSON.parse(localStorage.getItem('promptHistory') || '[]');
    hist.unshift({
        profession: payload.profession,
        feature: payload.feature,
        description: payload.description,
        response: responseText, // Salva a resposta completa em texto
        date: new Date().toLocaleString('pt-BR')
    });

    if (hist.length > 10) hist = hist.slice(0, 10);
    localStorage.setItem('promptHistory', JSON.stringify(hist));
    renderHistory();
}

/**
 * Renderiza o histórico na página.
 */
function renderHistory() {
    const historyList = document.getElementById('historyList');
    if (!historyList) return;

    let hist = JSON.parse(localStorage.getItem('promptHistory') || '[]');
    historyList.innerHTML = '';

    hist.forEach(item => {
        const div = document.createElement('div');
        div.className = 'history-item';
        div.innerHTML = `<b>${item.date}</b><br><code>${item.profession} / ${item.feature}</code>`;

        div.onclick = () => {
            document.getElementById('professionSelect').value = item.profession;
            // Recarregar features pode ser necessário se a seleção de profissão não disparar o 'onchange'
            loadFeatures().then(() => {
                document.getElementById('featureSelect').value = item.feature;
            });
            document.getElementById('requestDescription').value = item.description;
            
            const resultEl = document.getElementById('promptAnalyzeResult');
            const mdResultEl = document.getElementById('mdResultProf');

            try {
                const json = JSON.parse(item.response);
                resultEl.textContent = JSON.stringify(json, null, 2);
                let md = json?.candidates?.[0]?.content?.parts?.[0]?.text || '';
                mdResultEl.innerHTML = window.marked ? marked.parse(md) : md;
            } catch {
                resultEl.textContent = item.response;
                mdResultEl.innerHTML = '';
            }
        };
        historyList.appendChild(div);
    });
}

/**
 * Carrega as profissões da API.
 */
async function loadProfessions() {
    try {
        const resp = await fetch('/api/professions');
        const data = await resp.json();
        const select = document.getElementById('professionSelect');
        select.innerHTML = '<option value="">Selecione...</option>';
        data.forEach(p => {
            const opt = document.createElement('option');
            opt.value = p.id;
            opt.textContent = p.name;
            opt.dataset.features = JSON.stringify(p.features || []);
            select.appendChild(opt);
        });
    } catch (err) {
        console.error("Falha ao carregar profissões:", err);
    }
}

/**
 * Carrega as funcionalidades baseadas na profissão selecionada.
 */
async function loadFeatures() {
    const featureSelect = document.getElementById('featureSelect');
    const professionSelect = document.getElementById('professionSelect');
    featureSelect.innerHTML = '<option value="">Selecione...</option>';
    
    const selectedOption = professionSelect.options[professionSelect.selectedIndex];
    if (!selectedOption || !selectedOption.dataset.features) return;
    
    const features = JSON.parse(selectedOption.dataset.features);
    features.forEach(f => {
        const opt = document.createElement('option');
        opt.value = f.name; // Assumindo que o valor é o nome
        opt.textContent = f.name;
        featureSelect.appendChild(opt);
    });
}


document.addEventListener('DOMContentLoaded', () => {
    // Inicialização da página
    loadProfessions();
    renderHistory();

    const professionSelect = document.getElementById('professionSelect');
    if (professionSelect) {
        professionSelect.onchange = loadFeatures;
    }

    const promptAnalyzeBtn = document.getElementById('promptAnalyzeBtn');
    if (promptAnalyzeBtn) {
        promptAnalyzeBtn.onclick = async () => {
            const professionId = document.getElementById('professionSelect').value;
            const professionName = document.getElementById('professionSelect').selectedOptions[0]?.textContent;
            const feature = document.getElementById('featureSelect').value;
            const description = document.getElementById('requestDescription').value.trim();

            const msgEl = document.getElementById('promptAnalyzeMsg');
            const resultEl = document.getElementById('promptAnalyzeResult');
            const mdResultEl = document.getElementById('mdResultProf');

            if (!professionId || !feature || !description) {
                msgEl.innerHTML = '<span class="error">Preencha todos os campos.</span>';
                return;
            }

            msgEl.innerHTML = 'Enviando...';
            resultEl.textContent = '';
            mdResultEl.innerHTML = '';

            const payload = { profession: professionName, feature, description };

            try {
                const resp = await fetch('/v1/prompt-analyze', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                const text = await resp.text();
                
                if (!resp.ok) {
                   throw new Error(text);
                }

                msgEl.innerHTML = '<span class="success">Resposta recebida!</span>';
                const json = JSON.parse(text);
                resultEl.textContent = JSON.stringify(json, null, 2);

                let md = '';
                if (json?.candidates?.[0]?.content?.parts?.[0]?.text) {
                    md = json.candidates[0].content.parts[0].text;
                }
                mdResultEl.innerHTML = window.marked ? marked.parse(md) : md;
                
                // BUG FIX: Salva no histórico APENAS no fluxo correto
                saveHistory(payload, text);

            } catch (err) {
                msgEl.innerHTML = `<span class="error">Erro: ${err.message}</span>`;
                resultEl.textContent = err.message;
            }
        };
    }
});