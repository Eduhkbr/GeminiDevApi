package com.eduhkbr.gemini.DevApi.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;

@Service
public class PromptTemplateService {
    @Value("classpath:prompt-template.txt")
    private Resource templateResource;

    public String buildPrompt(String profession, String feature, String description) {
        try {
            String template;
            try (var in = templateResource.getInputStream()) {
                template = new String(in.readAllBytes(), StandardCharsets.UTF_8);
            }
            return template.replace("{profissao}", profession)
                           .replace("{funcionalidade}", feature)
                           .replace("{descricao}", description);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao carregar template de prompt", e);
        }
    }
}
