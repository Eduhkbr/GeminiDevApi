package com.eduhkbr.gemini.DevApi.web.dto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JavaClassDTOTest {
    @Test
    void gettersESetters() {
        JavaClassDTO dto = new JavaClassDTO();
        dto.setName("ClasseTeste");
        dto.setSourceCode("public class ClasseTeste {}");
        assertEquals("ClasseTeste", dto.getName());
        assertEquals("public class ClasseTeste {}", dto.getSourceCode());
    }
}
