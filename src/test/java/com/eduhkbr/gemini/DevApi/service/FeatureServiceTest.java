package com.eduhkbr.gemini.DevApi.service;

import com.eduhkbr.gemini.DevApi.model.Feature;
import com.eduhkbr.gemini.DevApi.model.Profession;
import com.eduhkbr.gemini.DevApi.repository.FeatureRepository;
import com.eduhkbr.gemini.DevApi.repository.ProfessionRepository;
import com.eduhkbr.gemini.DevApi.web.dto.FeatureRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    @DisplayName("findAll deve retornar uma lista de Features")
    void findAll_shouldReturnListOfFeatures() {
        when(featureRepository.findAll()).thenReturn(List.of(new Feature(), new Feature()));

        List<Feature> result = featureService.findAll();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(featureRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("create deve ter sucesso quando a profissão existe")
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
    @DisplayName("create deve lançar exceção quando a profissão não é encontrada")
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

    @Test
    @DisplayName("create deve ter sucesso com profissão nula")
    void create_withNullProfessionId_shouldSucceed() {
        // Arrange
        FeatureRequestDTO dto = new FeatureRequestDTO();
        dto.setName("Feature sem Profissão");
        dto.setProfessionId(null); // Cenário onde a feature não tem profissão

        Feature savedFeature = new Feature();
        savedFeature.setId(11L);
        savedFeature.setName("Feature sem Profissão");

        when(featureRepository.save(any(Feature.class))).thenReturn(savedFeature);

        Feature result = featureService.create(dto);

        assertNotNull(result);
        assertNull(result.getProfession()); // Garante que a profissão é nula
        verify(professionRepository, never()).findById(any()); // Verifica que findById nunca foi chamado
    }
    
    @Test
    @DisplayName("create deve lançar exceção para nome inválido")
    void create_withInvalidName_shouldThrowException() {
        FeatureRequestDTO dto = new FeatureRequestDTO();
        dto.setName(" ");

        assertThrows(IllegalArgumentException.class, () -> {
            featureService.create(dto);
        });
        verify(featureRepository, never()).save(any());
    }

    @Test
    @DisplayName("update deve ter sucesso quando a feature e a profissão existem")
    void update_whenFeatureAndProfessionExist_shouldSucceed() {
        Long featureId = 1L;
        FeatureRequestDTO dto = new FeatureRequestDTO();
        dto.setName("Nome Atualizado");
        dto.setProfessionId(2L);

        Feature existingFeature = new Feature();
        existingFeature.setId(featureId);
        existingFeature.setName("Nome Antigo");

        Profession newProfession = new Profession();
        newProfession.setId(2L);

        when(featureRepository.findById(featureId)).thenReturn(Optional.of(existingFeature));
        when(professionRepository.findById(2L)).thenReturn(Optional.of(newProfession));
        when(featureRepository.save(any(Feature.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Feature result = featureService.update(featureId, dto);

        assertNotNull(result);
        assertEquals("Nome Atualizado", result.getName());
        assertEquals(2L, result.getProfession().getId());
    }

    @Test
    @DisplayName("update deve lançar exceção quando a feature não é encontrada")
    void update_whenFeatureNotFound_shouldThrowException() {
        Long nonExistentId = 99L;
        FeatureRequestDTO dto = new FeatureRequestDTO();
        dto.setName("Qualquer Nome");
        when(featureRepository.findById(nonExistentId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            featureService.update(nonExistentId, dto);
        });
    }

    @Test
    @DisplayName("delete deve chamar o repositório quando a feature existe")
    void delete_whenFeatureExists_shouldCallRepositoryDelete() {
        // Arrange
        Long featureId = 1L;
        when(featureRepository.existsById(featureId)).thenReturn(true);
        doNothing().when(featureRepository).deleteById(featureId);

        featureService.delete(featureId);

        // Verifica se deleteById foi chamado exatamente uma vez com o ID correto
        verify(featureRepository, times(1)).deleteById(featureId);
    }
    
    @Test
    @DisplayName("delete deve lançar exceção quando a feature não é encontrada")
    void delete_whenFeatureNotFound_shouldThrowException() {
        Long nonExistentId = 99L;
        when(featureRepository.existsById(nonExistentId)).thenReturn(false);

        assertThrows(EntityNotFoundException.class, () -> {
            featureService.delete(nonExistentId);
        });
        // Garante que deleteById nunca seja chamado se a feature não existe
        verify(featureRepository, never()).deleteById(anyLong());
    }
}