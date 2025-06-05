package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.GenerationCache;
import com.eduhkbr.gemini.DevApi.repository.GenerationCacheRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GenerationCacheControllerTest {
    private GenerationCacheRepository repository;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        repository = mock(GenerationCacheRepository.class);
        GenerationCacheController controller = new GenerationCacheController(repository);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void testListAllWithEmptyList() throws Exception {
        when(repository.findAll()).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/v1/cache").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testListAllWithSingleItemShortResult() throws Exception {
        GenerationCache cache = new GenerationCache();
        cache.setId(1L);
        cache.setName("Teste");
        cache.setCreatedAt(java.time.Instant.now());
        cache.setResult("resultado curto");
        when(repository.findAll()).thenReturn(List.of(cache));
        mockMvc.perform(get("/v1/cache").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Teste")))
                .andExpect(jsonPath("$[0].resultSummary", is("resultado curto")));
    }

    @Test
    void testListAllWithLongResult() throws Exception {
        GenerationCache cache = new GenerationCache();
        cache.setId(2L);
        cache.setName("Longo");
        cache.setCreatedAt(java.time.Instant.now());
        String longResult = "a".repeat(120);
        cache.setResult(longResult);
        when(repository.findAll()).thenReturn(List.of(cache));
        mockMvc.perform(get("/v1/cache").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].resultSummary", org.hamcrest.Matchers.startsWith("aaaaaaaaaa")))
                .andExpect(jsonPath("$[0].resultSummary", org.hamcrest.Matchers.endsWith("...")))
                .andExpect(jsonPath("$[0].resultSummary", hasLength(103)));
    }
}
