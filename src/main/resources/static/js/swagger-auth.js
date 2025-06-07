window.addEventListener('load', () => {
    // Um pequeno atraso para garantir que a UI do Swagger esteja totalmente renderizada
    setTimeout(function() {
        // Pega a instância da UI do Swagger que fica disponível globalmente
        const ui = window.ui;
        if (!ui) {
            console.error("Swagger UI não encontrada.");
            return;
        }

        // Pega o token JWT do sessionStorage da nossa aplicação principal
        const jwtToken = sessionStorage.getItem('jwt');

        if (jwtToken) {
            console.log("Token JWT encontrado, tentando auto-autorizar o Swagger UI...");
            
            // Usa a API interna do Swagger para definir o token para o esquema de segurança
            // O nome 'bearerAuth' deve ser o mesmo que definimos na anotação @SecurityScheme
            ui.preauthorizeApiKey("bearerAuth", `Bearer ${jwtToken}`);

            console.log("Swagger UI auto-autorizado com sucesso.");
        } else {
            console.log("Nenhum token JWT encontrado no sessionStorage para auto-autorização.");
        }

    }, 1000);
});