package com.eduhkbr.gemini.DevApi.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.context.ActiveProfiles;
import com.eduhkbr.gemini.DevApi.repository.GenerationCacheRepository;
import org.springframework.cache.CacheManager;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("dev")
class GenerationControllerCacheIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private GenerationCacheRepository cacheRepository;
    @Autowired
    private CacheManager cacheManager;

    @Test
    void testAnalyze_usesCacheAndDb_andMetrics() throws Exception {
        // Limpa cache e banco antes do teste
        cacheRepository.deleteAll();
        var cache = cacheManager.getCache("generationCache");
        if (cache != null) {
            cache.clear();
        }
        String payload = "[{'name':'OrderService','sourceCode':'public class OrderService {}'}]".replace("'", "\"");
        // Primeira chamada: deve acionar IA e persistir
        MvcResult firstResult = mockMvc.perform(post("/v1/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].documentation", not(emptyOrNullString())))
                .andReturn();
        String firstResponse = firstResult.getResponse().getContentAsString();
        // Segunda chamada: deve usar cache/banco, não acionar IA
        MvcResult secondResult = mockMvc.perform(post("/v1/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(payload))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].documentation", not(emptyOrNullString())))
                .andReturn();
        String secondResponse = secondResult.getResponse().getContentAsString();
        // O resultado deve ser igual (indicando uso de cache ou banco)
        assertEquals(firstResponse, secondResponse, "A resposta da segunda chamada deve ser igual à primeira (cache ou banco)");
        // Consulta métricas customizadas
        MvcResult metricsResult = mockMvc.perform(MockMvcRequestBuilders.get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andReturn();
        String metrics = metricsResult.getResponse().getContentAsString();
        assertTrue(metrics.contains("gemini_cache_hit_total"), "Métrica de cache hit deve estar presente");
        assertTrue(metrics.contains("gemini_ia_call_total"), "Métrica de chamada à IA deve estar presente");
    }
}
