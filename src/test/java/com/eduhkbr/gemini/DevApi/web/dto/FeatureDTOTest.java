package com.eduhkbr.gemini.DevApi.web.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FeatureDTOTest {
    @Test
    void testGetSetName() {
        FeatureDTO dto = new FeatureDTO();
        dto.setName("Teste");
        assertEquals("Teste", dto.getName());
    }

    @Test
    void testGetSetProfessionId() {
        FeatureDTO dto = new FeatureDTO();
        dto.setProfessionId(123L);
        assertEquals(123L, dto.getProfessionId());
    }

    @Test
    void testDefaultValues() {
        FeatureDTO dto = new FeatureDTO();
        assertNull(dto.getName());
        assertNull(dto.getProfessionId());
    }
}
