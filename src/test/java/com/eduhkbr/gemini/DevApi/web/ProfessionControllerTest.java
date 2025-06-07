package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.Feature;
import com.eduhkbr.gemini.DevApi.model.Profession;
import com.eduhkbr.gemini.DevApi.service.ProfessionService;
import com.eduhkbr.gemini.DevApi.util.JwtUtil;
import com.eduhkbr.gemini.DevApi.web.dto.ProfessionRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfessionController.class)
class ProfessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfessionService professionService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;
    
    // --- TESTES PARA GET /api/professions ---

    @Test
    @WithMockUser // Requer apenas um usuário logado
    void getAll_shouldReturnListOfProfessions() throws Exception {
        // Arrange
        Profession profession = new Profession();
        profession.setId(1L);
        profession.setName("DBA");

        given(professionService.findAll()).willReturn(Collections.singletonList(profession));

        // Act & Assert
        mockMvc.perform(get("/api/professions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("DBA"))
                .andExpect(jsonPath("$[0].features").isArray()); // Verifica se features existe, mesmo que vazio
    }
    
    // --- TESTES PARA GET /api/professions/{id} ---

    @Test
    @WithMockUser
    void getById_whenProfessionExists_shouldReturnProfession() throws Exception {
        // Arrange
        Profession profession = new Profession();
        profession.setId(1L);
        profession.setName("Engenheiro de QA");
        
        given(professionService.findById(1L)).willReturn(profession);

        // Act & Assert
        mockMvc.perform(get("/api/professions/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Engenheiro de QA"));
    }

    @Test
    @WithMockUser
    void getById_whenProfessionNotFound_shouldReturn404() throws Exception {
        // Arrange
        given(professionService.findById(99L)).willThrow(new EntityNotFoundException());

        // Act & Assert
        mockMvc.perform(get("/api/professions/{id}", 99L))
                .andExpect(status().isNotFound());
    }

    // --- TESTES PARA POST /api/professions ---

    @Test
    @WithMockUser(roles = "ADMIN") // Requer um ADMIN
    void create_shouldReturn201Created() throws Exception {
        // Arrange
        ProfessionRequestDTO dto = new ProfessionRequestDTO();
        dto.setName("Engenheiro de Dados");

        Profession savedProfession = new Profession();
        savedProfession.setId(1L);
        savedProfession.setName("Engenheiro de Dados");

        given(professionService.create(any(ProfessionRequestDTO.class))).willReturn(savedProfession);

        // Act & Assert
        mockMvc.perform(post("/api/professions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Engenheiro de Dados"));
    }
    
    @Test
    @WithMockUser(roles = "ADMIN")
    void create_withInvalidData_shouldReturn400BadRequest() throws Exception {
        // Arrange
        ProfessionRequestDTO dto = new ProfessionRequestDTO();
        dto.setName(""); // Nome inválido para acionar a validação @NotBlank

        // Act & Assert
        mockMvc.perform(post("/api/professions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    // --- TESTES PARA PUT /api/professions/{id} ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_whenProfessionExists_shouldReturn200Ok() throws Exception {
        // Arrange
        Long id = 1L;
        ProfessionRequestDTO dto = new ProfessionRequestDTO();
        dto.setName("Arquiteto de Soluções");

        Profession updatedProfession = new Profession();
        updatedProfession.setId(id);
        updatedProfession.setName("Arquiteto de Soluções");

        given(professionService.update(eq(id), any(ProfessionRequestDTO.class))).willReturn(updatedProfession);

        // Act & Assert
        mockMvc.perform(put("/api/professions/{id}", id)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Arquiteto de Soluções"));
    }

    // --- TESTES PARA DELETE /api/professions/{id} ---

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_whenProfessionExists_shouldReturn204NoContent() throws Exception {
        // Arrange
        Long id = 1L;
        // Configura o mock para um método void
        doNothing().when(professionService).delete(id);

        // Act & Assert
        mockMvc.perform(delete("/api/professions/{id}", id)
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_whenProfessionNotFound_shouldReturn404() throws Exception {
        // Arrange
        Long id = 99L;
        // Configura o mock para lançar a exceção quando o ID não existe
        doThrow(new EntityNotFoundException()).when(professionService).delete(id);

        // Act & Assert
        mockMvc.perform(delete("/api/professions/{id}", id)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}