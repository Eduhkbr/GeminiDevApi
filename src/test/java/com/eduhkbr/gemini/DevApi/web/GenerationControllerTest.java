package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.GenerationResult;
import com.eduhkbr.gemini.DevApi.model.JavaClass;
import com.eduhkbr.gemini.DevApi.service.JavaClassAnalyzerService;
import com.eduhkbr.gemini.DevApi.web.dto.JavaClassDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GenerationControllerTest {
    @Mock
    private JavaClassAnalyzerService analyzer;

    @InjectMocks
    private GenerationController generationController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testAnalyze() {
        JavaClassDTO dto = new JavaClassDTO();
        dto.setName("TestClass");
        dto.setSourceCode("public class TestClass {}");
        JavaClass javaClass = new JavaClass(dto.getName(), dto.getSourceCode());
        GenerationResult result = mock(GenerationResult.class);
        when(analyzer.analyze(any(JavaClass.class))).thenReturn(result);
        ResponseEntity<?> response = generationController.analyze(dto);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(result, response.getBody());
        verify(analyzer, times(1)).analyze(any(JavaClass.class));
    }

    @Test
    void testAnalyze_NullDto() {
        ResponseEntity<?> response = generationController.analyze(null);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Dados obrigatórios ausentes"));
    }

    @Test
    void testAnalyze_MissingName() {
        JavaClassDTO dto = new JavaClassDTO();
        dto.setSourceCode("public class TestClass {}");
        ResponseEntity<?> response = generationController.analyze(dto);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Dados obrigatórios ausentes"));
    }

    @Test
    void testAnalyze_MissingSourceCode() {
        JavaClassDTO dto = new JavaClassDTO();
        dto.setName("TestClass");
        ResponseEntity<?> response = generationController.analyze(dto);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Dados obrigatórios ausentes"));
    }

    @Test
    void testAnalyze_NameTooLong() {
        JavaClassDTO dto = new JavaClassDTO();
        dto.setName("A".repeat(101));
        dto.setSourceCode("public class TestClass {}");
        ResponseEntity<?> response = generationController.analyze(dto);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Nome ou código muito longo"));
    }

    @Test
    void testAnalyze_SourceCodeTooLong() {
        JavaClassDTO dto = new JavaClassDTO();
        dto.setName("TestClass");
        dto.setSourceCode("A".repeat(20001));
        ResponseEntity<?> response = generationController.analyze(dto);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Nome ou código muito longo"));
    }

    @Test
    void testAnalyze_InvalidName() {
        JavaClassDTO dto = new JavaClassDTO();
        dto.setName("Test<Class>");
        dto.setSourceCode("public class TestClass {}");
        ResponseEntity<?> response = generationController.analyze(dto);
        assertEquals(400, response.getStatusCodeValue());
        assertTrue(response.getBody().toString().contains("Nome inválido"));
    }
}
