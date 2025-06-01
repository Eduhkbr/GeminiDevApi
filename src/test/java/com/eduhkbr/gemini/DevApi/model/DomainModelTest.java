package com.eduhkbr.gemini.DevApi.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class JavaClassTest {
    @Test
    @DisplayName("Deve criar JavaClass corretamente e retornar valores")
    void construtorEGetters() {
        JavaClass jc = new JavaClass("ClasseTeste", "public class ClasseTeste {}");
        assertEquals("ClasseTeste", jc.getName());
        assertEquals("public class ClasseTeste {}", jc.getSourceCode());
    }
}

class GenerationResultTest {
    @Test
    @DisplayName("Deve criar GenerationResult corretamente e retornar valores")
    void construtorEGetters() {
        GenerationResult gr = new GenerationResult("doc", "test");
        assertEquals("doc", gr.getDocumentation());
        assertEquals("test", gr.getTestSkeleton());
    }
}

class GenerationCacheTest {
    @Test
    @DisplayName("Deve criar e manipular GenerationCache corretamente")
    void settersEGetters() {
        GenerationCache cache = new GenerationCache();
        cache.setId(1L);
        cache.setHash("hash123");
        cache.setName("Classe");
        cache.setSourceCode("codigo");
        cache.setResult("resultado");
        Instant now = Instant.now();
        cache.setCreatedAt(now);
        assertEquals(1L, cache.getId());
        assertEquals("hash123", cache.getHash());
        assertEquals("Classe", cache.getName());
        assertEquals("codigo", cache.getSourceCode());
        assertEquals("resultado", cache.getResult());
        assertEquals(now, cache.getCreatedAt());
    }
}
