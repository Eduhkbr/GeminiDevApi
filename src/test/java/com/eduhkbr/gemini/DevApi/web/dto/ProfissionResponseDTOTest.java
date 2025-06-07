package com.eduhkbr.gemini.DevApi.web.dto;

import com.eduhkbr.gemini.DevApi.model.Feature;
import com.eduhkbr.gemini.DevApi.model.Profession;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class ProfessionResponseDTOTest {

    @Test
    void fromEntity_whenProfessionHasFeatures_shouldMapFeaturesCorrectly() {
        
        Profession profession = new Profession();
        profession.setId(1L);
        profession.setName("Desenvolvedor Backend");

        Feature feature1 = new Feature();
        feature1.setId(101L);
        feature1.setName("Criar APIs REST");
        
        Feature feature2 = new Feature();
        feature2.setId(102L);
        feature2.setName("Conectar ao Banco de Dados");
        
        profession.setFeatures(List.of(feature1, feature2));
        
        ProfessionResponseDTO dto = ProfessionResponseDTO.fromEntity(profession);
        
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Desenvolvedor Backend", dto.name());
        assertNotNull(dto.features());
        assertEquals(2, dto.features().size());
        assertEquals("Criar APIs REST", dto.features().get(0).name());
        assertEquals(102L, dto.features().get(1).id());
    }

    @Test
    void fromEntity_whenProfessionHasNoFeatures_shouldReturnEmptyList() {
        Profession profession = new Profession();
        profession.setId(2L);
        profession.setName("DBA");
        profession.setFeatures(Collections.emptyList());

        ProfessionResponseDTO dto = ProfessionResponseDTO.fromEntity(profession);

        assertNotNull(dto);
        assertEquals(2L, dto.id());
        assertNotNull(dto.features());
        assertTrue(dto.features().isEmpty());
    }
}