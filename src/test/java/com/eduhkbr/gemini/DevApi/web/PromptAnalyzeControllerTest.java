package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.service.GeminiService;
import com.eduhkbr.gemini.DevApi.service.PromptTemplateService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PromptAnalyzeControllerTest {
    private MockMvc mockMvc;

    @Mock
    private GeminiService geminiService;
    @Mock
    private PromptTemplateService promptTemplateService;
    @InjectMocks
    private PromptAnalyzeController promptAnalyzeController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(promptAnalyzeController).build();
    }

    @Test
    void analyze_DeveRetornarTextoGerado() throws Exception {
        String profession = "Engenheiro";
        String feature = "Resumo";
        String description = "Descreva um sistema.";
        String prompt = "Prompt gerado";
        String result = "Texto da IA";

        when(promptTemplateService.buildPrompt(eq(profession), eq(feature), eq(description))).thenReturn(prompt);
        when(geminiService.sendPrompt(eq(prompt))).thenReturn(result);

        String json = String.format("{\"profession\":\"%s\",\"feature\":\"%s\",\"description\":\"%s\"}", profession, feature, description);

        mockMvc.perform(post("/v1/prompt-analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string(result));
    }
}
