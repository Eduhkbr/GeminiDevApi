package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.JavaClass;
import com.eduhkbr.gemini.DevApi.model.GenerationResult;
import com.eduhkbr.gemini.DevApi.service.JavaClassAnalyzerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class GenerationControllerCacheIT {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void testAnalyze_usesCacheAndDb() throws Exception {
        String payload = "[{'name':'OrderService','sourceCode':'public class OrderService {}'}]".replace("'", "\"");
        // Primeira chamada: deve acionar IA e persistir
        ResultActions first = mockMvc.perform(post("/v1/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].documentation", not(emptyOrNullString())));
        // Segunda chamada: deve usar cache/banco, não acionar IA
        ResultActions second = mockMvc.perform(post("/v1/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].documentation", not(emptyOrNullString())));
    }
}
