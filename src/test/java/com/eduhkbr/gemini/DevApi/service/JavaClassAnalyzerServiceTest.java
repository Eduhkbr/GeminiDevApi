package com.eduhkbr.gemini.DevApi.service;

import com.eduhkbr.gemini.DevApi.llm.LlmClient;
import com.eduhkbr.gemini.DevApi.model.JavaClass;
import com.eduhkbr.gemini.DevApi.model.GenerationResult;
import com.eduhkbr.gemini.DevApi.repository.GenerationCacheRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JavaClassAnalyzerServiceTest {
    private final LlmClient llmClient = mock(LlmClient.class);
    private final GenerationCacheRepository cacheRepository = mock(GenerationCacheRepository.class);
    private final MeterRegistry meterRegistry = mock(MeterRegistry.class);
    private final Counter cacheHitCounter = mock(Counter.class);
    private final Counter iaCallCounter = mock(Counter.class);
    private final JavaClassAnalyzerService service;

    public JavaClassAnalyzerServiceTest() {
        when(meterRegistry.counter("generation.cache.hits")).thenReturn(cacheHitCounter);
        when(meterRegistry.counter("generation.ia.calls")).thenReturn(iaCallCounter);
        service = new JavaClassAnalyzerService(llmClient, cacheRepository, meterRegistry);
    }

    @Test
    void testAnalyze_QuandoClasseValida_DeveRetornarResultado() throws Exception {
        // TODO: Arrange
        JavaClass javaClass = new JavaClass("MinhaClasse", "public class MinhaClasse {}");
        when(llmClient.sendPrompt(anyString())).thenReturn("doc", "test");
        // Simula cache vazio
        when(cacheRepository.findByHash(anyString())).thenReturn(java.util.Optional.empty());
        // Setando os templates via reflexão
        Field docTemplateField = JavaClassAnalyzerService.class.getDeclaredField("docTemplate");
        docTemplateField.setAccessible(true);
        docTemplateField.set(service, "Doc: %s %s");
        Field testTemplateField = JavaClassAnalyzerService.class.getDeclaredField("testTemplate");
        testTemplateField.setAccessible(true);
        testTemplateField.set(service, "Test: %s %s");
        // TODO: Act
        GenerationResult result = service.analyze(javaClass);
        // TODO: Assert
        assertNotNull(result);
        assertEquals("doc", result.getDocumentation());
        assertEquals("test", result.getTestSkeleton());
    }
}
