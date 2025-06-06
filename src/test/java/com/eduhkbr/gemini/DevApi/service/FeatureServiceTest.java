package com.eduhkbr.gemini.DevApi.service;

import com.eduhkbr.gemini.DevApi.model.Feature;
import com.eduhkbr.gemini.DevApi.model.Profession;
import com.eduhkbr.gemini.DevApi.repository.FeatureRepository;
import com.eduhkbr.gemini.DevApi.repository.ProfessionRepository;
import com.eduhkbr.gemini.DevApi.web.dto.FeatureRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FeatureServiceTest {

    @Mock
    private FeatureRepository featureRepository;

    @Mock
    private ProfessionRepository professionRepository;

    @InjectMocks
    private FeatureService featureService;

    @Test
    void create_whenProfessionExists_shouldSucceed() {
        FeatureRequestDTO dto = new FeatureRequestDTO();
        dto.setName("Nova Feature");
        dto.setProfessionId(1L);

        Profession mockProfession = new Profession();
        mockProfession.setId(1L);
        mockProfession.setName("Engenheiro de Software");

        Feature savedFeature = new Feature();
        savedFeature.setId(10L);
        savedFeature.setName("Nova Feature");
        savedFeature.setProfession(mockProfession);

        when(professionRepository.findById(1L)).thenReturn(Optional.of(mockProfession));
        when(featureRepository.save(any(Feature.class))).thenReturn(savedFeature);

        Feature result = featureService.create(dto);

        assertNotNull(result);
        assertEquals("Nova Feature", result.getName());
        assertEquals(1L, result.getProfession().getId());
        
        verify(professionRepository, times(1)).findById(1L);
        verify(featureRepository, times(1)).save(any(Feature.class));
    }

    @Test
    void create_whenProfessionNotFound_shouldThrowException() {
        FeatureRequestDTO dto = new FeatureRequestDTO();
        dto.setName("Feature com Profissão Inválida");
        dto.setProfessionId(99L); 

        when(professionRepository.findById(99L)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, () -> {
            featureService.create(dto);
        });

        assertEquals("Profissão não encontrada com o ID: 99", exception.getMessage());

        verify(featureRepository, never()).save(any(Feature.class));
    }
}