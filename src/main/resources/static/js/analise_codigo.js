// ===================================
// LÓGICA - ANÁLISE DE CÓDIGO
// ===================================

document.addEventListener('DOMContentLoaded', () => {
    const analyzeBtn = document.getElementById('analyzeBtn');
    const jsonResult = document.getElementById('jsonResult');
    const mdResult = document.getElementById('mdResult');
    const analyzeMsg = document.getElementById('analyzeMsg');

    if (analyzeBtn) {
        analyzeBtn.onclick = async () => {
            const name = document.getElementById('className').value.trim();
            const sourceCode = document.getElementById('classCode').value.trim();

            if (!name || !sourceCode) {
                analyzeMsg.innerHTML = '<span class="error">Preencha o nome e o código da classe.</span>';
                return;
            }

            const payload = { name, sourceCode };
            analyzeMsg.innerHTML = '<span class="success">Analisando...</span>';
            jsonResult.textContent = '';
            mdResult.innerHTML = '';

            try {
                const resp = await fetch('/v1/analyze', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(payload)
                });

                const text = await resp.text();
                
                if (!resp.ok) {
                   throw new Error(`Erro ${resp.status}: ${text}`);
                }

                const json = JSON.parse(text);
                jsonResult.textContent = JSON.stringify(json, null, 2);

                let md = '';
                if (json?.candidates?.[0]?.content?.parts?.[0]?.text) {
                    md = json.candidates[0].content.parts[0].text;
                }

                mdResult.innerHTML = window.marked ? marked.parse(md) : md;
                analyzeMsg.innerHTML = '<span class="success">Análise concluída!</span>';

            } catch (err) {
                analyzeMsg.innerHTML = `<span class="error">Erro na requisição: ${err.message}</span>`;
                jsonResult.textContent = err.message;
            }
        };
    }
});