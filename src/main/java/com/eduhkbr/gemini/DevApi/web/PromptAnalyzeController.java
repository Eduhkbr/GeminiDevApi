package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.service.GeminiService;
import com.eduhkbr.gemini.DevApi.service.PromptTemplateService;
import com.eduhkbr.gemini.DevApi.web.dto.PromptRequestDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;

@Validated
@RestController
@RequestMapping("/v1/prompt-analyze")
public class PromptAnalyzeController {
    private static final Logger logger = LoggerFactory.getLogger(PromptAnalyzeController.class);
    private final GeminiService geminiService;
    private final PromptTemplateService promptTemplateService;

    public PromptAnalyzeController(PromptTemplateService promptTemplateService, GeminiService geminiService) {
        this.geminiService = geminiService;
        this.promptTemplateService = promptTemplateService;
    }

    @PostMapping(produces = MediaType.TEXT_PLAIN_VALUE, headers = "Accept=text/plain")
    public ResponseEntity<String> analyze(@Valid @RequestBody PromptRequestDTO dto) {
        // Log seguro: não loga dados controlados pelo usuário diretamente
        logger.info("Recebido: profession and feature recebidos com sucesso");
        String profession = dto.getProfession().replaceAll("[<>\"'\\\\]", "");
        String feature = dto.getFeature().replaceAll("[<>\"'\\\\]", "");
        String description = dto.getDescription().replaceAll("[<>\"'\\\\]", "");
        String prompt = promptTemplateService.buildPrompt(profession, feature, description);
        String result = geminiService.sendPrompt(prompt);
        logger.info("Resultado da IA gerado com sucesso");
        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_PLAIN)
                .body(result);
    }
}
