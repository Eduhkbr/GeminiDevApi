package com.eduhkbr.gemini.DevApi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class GeminiService {
    @Value("${GEMINI_API_URL:https://api.fake-gemini.com/v1/generate}")
    private String geminiApiUrl;
    @Value("${GEMINI_API_KEY:}")
    private String geminiApiKey;

    // Implemente a integração real com a API Gemini aqui
    public String sendPrompt(String prompt) {
        if (geminiApiKey == null || geminiApiKey.isBlank()) {
            // Retorna mock se não configurado
            return "[RESPOSTA MOCK GEMINI]\n" + prompt;
        }
        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + geminiApiKey);
            String body = "{\"prompt\": " + toJsonString(prompt) + "}";
            HttpEntity<String> entity = new HttpEntity<>(body, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(geminiApiUrl, entity, String.class);
            return response.getBody();
        } catch (Exception e) {
            return "[ERRO AO CHAMAR GEMINI] " + e.getMessage();
        }
    }

    private String toJsonString(String s) {
        return "\"" + s.replace("\"", "\\\"").replace("\n", "\\n") + "\"";
    }
}
