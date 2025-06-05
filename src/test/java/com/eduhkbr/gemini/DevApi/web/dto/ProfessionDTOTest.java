package com.eduhkbr.gemini.DevApi.web.dto;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ProfessionDTOTest {
    @Test
    void testGetSetName() {
        ProfessionDTO dto = new ProfessionDTO();
        dto.setName("Engenheiro");
        assertEquals("Engenheiro", dto.getName());
    }

    @Test
    void testDefaultValue() {
        ProfessionDTO dto = new ProfessionDTO();
        assertNull(dto.getName());
    }
}
