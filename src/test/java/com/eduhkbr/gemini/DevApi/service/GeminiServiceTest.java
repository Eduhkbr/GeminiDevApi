package com.eduhkbr.gemini.DevApi.service;

import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

class GeminiServiceTest {
    @Test
    void sendPrompt_deveRetornarMockQuandoApiKeyNaoConfigurada() {
        GeminiService service = new GeminiService();
        ReflectionTestUtils.setField(service, "geminiApiKey", "");
        String prompt = "Teste de prompt";
        String resposta = service.sendPrompt(prompt);
        assertTrue(resposta.contains("[RESPOSTA MOCK GEMINI]"));
        assertTrue(resposta.contains(prompt));
    }

    @Test
    void sendPrompt_deveRetornarErroQuandoExcecao() {
        GeminiService service = new GeminiService();
        ReflectionTestUtils.setField(service, "geminiApiKey", "chave-fake");
        ReflectionTestUtils.setField(service, "geminiApiUrl", "http://url-invalida");
        String resposta = service.sendPrompt("prompt");
        assertTrue(resposta.contains("[ERRO AO CHAMAR GEMINI]"));
    }

    @Test
    void toJsonString_deveEscaparCaracteres() {
        GeminiService service = new GeminiService();
        String original = "Texto \"com\" aspas\ne quebra";
        String esperado = "\"Texto \\\"com\\\" aspas\\ne quebra\"";
        String resultado = ReflectionTestUtils.invokeMethod(service, "toJsonString", original);
        assertEquals(esperado, resultado);
    }
}
