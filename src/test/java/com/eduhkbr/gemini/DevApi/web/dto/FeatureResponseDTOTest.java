package com.eduhkbr.gemini.DevApi.web.dto;

import com.eduhkbr.gemini.DevApi.model.Feature;
import com.eduhkbr.gemini.DevApi.model.Profession;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

// Este é um teste unitário puro, não precisa de anotações do Spring ou Mockito.
class FeatureResponseDTOTest {

    @Test
    void fromEntity_whenProfessionIsPresent_shouldMapAllFieldsCorrectly() {
        // Arrange (Dado que...)
        Profession profession = new Profession();
        profession.setId(10L);
        profession.setName("Cientista de Dados");

        Feature feature = new Feature();
        feature.setId(1L);
        feature.setName("Análise Preditiva");
        feature.setProfession(profession);

        // Act (Quando...)
        FeatureResponseDTO dto = FeatureResponseDTO.fromEntity(feature);

        // Assert (Então...)
        assertNotNull(dto);
        assertEquals(1L, dto.id());
        assertEquals("Análise Preditiva", dto.name());
        
        assertNotNull(dto.profession());
        assertEquals(10L, dto.profession().id());
        assertEquals("Cientista de Dados", dto.profession().name());
    }

    @Test
    void fromEntity_whenProfessionIsNull_shouldMapProfessionAsNull() {
        // Arrange (Dado que...)
        Feature feature = new Feature();
        feature.setId(2L);
        feature.setName("Tarefa Genérica");
        feature.setProfession(null); // A profissão é nula neste cenário

        // Act (Quando...)
        FeatureResponseDTO dto = FeatureResponseDTO.fromEntity(feature);

        // Assert (Então...)
        assertNotNull(dto);
        assertEquals(2L, dto.id());
        assertEquals("Tarefa Genérica", dto.name());
        
        // A asserção mais importante: a profissão no DTO também deve ser nula
        assertNull(dto.profession());
    }
}