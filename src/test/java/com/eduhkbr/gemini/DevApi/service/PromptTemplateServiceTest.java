package com.eduhkbr.gemini.DevApi.service;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class PromptTemplateServiceTest {
    @Test
    void buildPrompt_deveSubstituirVariaveisNoTemplate() throws Exception {
        PromptTemplateService service = new PromptTemplateService();
        // Usa o template real do classpath
        ReflectionTestUtils.setField(service, "templateResource", new ClassPathResource("prompt-template.txt"));
        String resultado = service.buildPrompt("Engenheiro", "Login", "Gerar tela de login");
        assertTrue(resultado.contains("Engenheiro"));
        assertTrue(resultado.contains("Login"));
        assertTrue(resultado.contains("Gerar tela de login"));
    }

    @Test
    void buildPrompt_deveLancarExcecaoSeTemplateNaoExiste() {
        PromptTemplateService service = new PromptTemplateService();
        // Força um resource inexistente
        ReflectionTestUtils.setField(service, "templateResource", new ClassPathResource("nao-existe.txt"));
        Exception ex = assertThrows(RuntimeException.class, () ->
            service.buildPrompt("Prof", "Func", "Desc")
        );
        assertTrue(ex.getMessage().contains("Erro ao carregar template"));
    }
}
