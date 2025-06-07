package com.eduhkbr.gemini.DevApi.service;

import com.eduhkbr.gemini.DevApi.model.Profession;
import com.eduhkbr.gemini.DevApi.repository.ProfessionRepository;
import com.eduhkbr.gemini.DevApi.web.dto.ProfessionRequestDTO;
import jakarta.persistence.EntityNotFoundException;
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
class ProfessionServiceTest {

    @Mock
    private ProfessionRepository professionRepository;

    @InjectMocks
    private ProfessionService professionService;

    @Test
    void findAll_shouldCallRepositoryFindAllWithFeatures() {
        Profession profession = new Profession();
        profession.setId(1L);
        profession.setName("DevOps");
        
        when(professionRepository.findAllWithFeatures()).thenReturn(List.of(profession));
        List<Profession> result = professionService.findAll();

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(professionRepository, times(1)).findAllWithFeatures();
    }

    @Test
    void create_shouldSaveAndReturnProfession() {
        ProfessionRequestDTO dto = new ProfessionRequestDTO();
        dto.setName("Arquiteto");

        Profession savedProfession = new Profession();
        savedProfession.setId(1L);
        savedProfession.setName("Arquiteto");
        
        when(professionRepository.save(any(Profession.class))).thenReturn(savedProfession);

        Profession result = professionService.create(dto);

        assertNotNull(result);
        assertEquals("Arquiteto", result.getName());
        verify(professionRepository).save(any(Profession.class));
    }
    
    @Test
    void update_whenProfessionExists_shouldUpdateAndReturnProfession() {
        Long id = 1L;
        ProfessionRequestDTO dto = new ProfessionRequestDTO();
        dto.setName("Arquiteto de Software");

        Profession existingProfession = new Profession();
        existingProfession.setId(id);
        existingProfession.setName("Arquiteto");

        when(professionRepository.findById(id)).thenReturn(Optional.of(existingProfession));
        when(professionRepository.save(any(Profession.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Profession result = professionService.update(id, dto);

        assertNotNull(result);
        assertEquals("Arquiteto de Software", result.getName());
    }

    @Test
    void update_whenProfessionNotFound_shouldThrowException() {
        Long id = 99L;
        ProfessionRequestDTO dto = new ProfessionRequestDTO();
        dto.setName("Inexistente");
        
        when(professionRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> {
            professionService.update(id, dto);
        });
        verify(professionRepository, never()).save(any());
    }
}