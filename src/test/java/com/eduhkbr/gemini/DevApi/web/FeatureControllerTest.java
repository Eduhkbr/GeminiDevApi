package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.Feature;
import com.eduhkbr.gemini.DevApi.model.Profession;
import com.eduhkbr.gemini.DevApi.service.FeatureService;
import com.eduhkbr.gemini.DevApi.util.JwtUtil;
import com.eduhkbr.gemini.DevApi.web.dto.FeatureRequestDTO;
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


@WebMvcTest(FeatureController.class)
class FeatureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FeatureService featureService;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- TESTES PARA GET /api/features ---
    @Test
    @WithMockUser // Requer apenas um usuário logado (USER ou ADMIN)
    void getAll_shouldReturnListOfFeatures() throws Exception {
        // Arrange
        Profession profession = new Profession();
        profession.setId(1L);
        profession.setName("Tester");
        Feature feature = new Feature();
        feature.setId(1L);
        feature.setName("Test Feature");
        feature.setProfession(profession);

        given(featureService.findAll()).willReturn(List.of(feature));

        // Act & Assert
        mockMvc.perform(get("/api/features"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("Test Feature"));
    }

    // --- TESTES PARA POST /api/features ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void create_withValidData_shouldReturn201Created() throws Exception {
        // Arrange
        FeatureRequestDTO dto = new FeatureRequestDTO();
        dto.setName("Test Feature");
        dto.setProfessionId(1L);

        Profession profession = new Profession();
        profession.setId(1L);
        profession.setName("Tester");

        Feature returnedFeature = new Feature();
        returnedFeature.setId(1L);
        returnedFeature.setName("Test Feature");
        returnedFeature.setProfession(profession);

        given(featureService.create(any(FeatureRequestDTO.class))).willReturn(returnedFeature);

        // Act & Assert
        mockMvc.perform(post("/api/features")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Test Feature"))
                .andExpect(jsonPath("$.profession.name").value("Tester"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_withInvalidData_shouldReturn400BadRequest() throws Exception {
        // Arrange
        FeatureRequestDTO dtoWithBlankName = new FeatureRequestDTO();
        dtoWithBlankName.setName("");
        dtoWithBlankName.setProfessionId(1L);

        // Act & Assert
        mockMvc.perform(post("/api/features")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoWithBlankName)))
                .andExpect(status().isBadRequest());
    }
    
    // --- TESTES PARA PUT /api/features/{id} ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void update_whenFeatureExists_shouldReturn200Ok() throws Exception {
        // Arrange
        long featureId = 1L;
        FeatureRequestDTO dto = new FeatureRequestDTO();
        dto.setName("Updated Feature Name");
        dto.setProfessionId(1L);
        
        Feature updatedFeature = new Feature();
        updatedFeature.setId(featureId);
        updatedFeature.setName("Updated Feature Name");

        given(featureService.update(eq(featureId), any(FeatureRequestDTO.class))).willReturn(updatedFeature);
        
        // Act & Assert
        mockMvc.perform(put("/api/features/{id}", featureId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Feature Name"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void update_whenFeatureNotFound_shouldReturn404NotFound() throws Exception {
        // Arrange
        long nonExistentId = 99L;
        FeatureRequestDTO dto = new FeatureRequestDTO();
        dto.setName("Any Name");
        
        // Configura o mock do serviço para lançar a exceção quando o ID não for encontrado
        given(featureService.update(eq(nonExistentId), any(FeatureRequestDTO.class)))
                .willThrow(new EntityNotFoundException("Feature não encontrada"));

        // Act & Assert
        mockMvc.perform(put("/api/features/{id}", nonExistentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound());
    }

    // --- TESTES PARA DELETE /api/features/{id} ---
    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_whenFeatureExists_shouldReturn204NoContent() throws Exception {
        // Arrange
        long featureId = 1L;
        // Configura o mock para um método void. Não faz nada quando delete(1L) é chamado.
        doNothing().when(featureService).delete(featureId);

        // Act & Assert
        mockMvc.perform(delete("/api/features/{id}", featureId)
                        .with(csrf()))
                .andExpect(status().isNoContent()); // Espera o status 204 No Content
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_whenFeatureNotFound_shouldReturn404NotFound() throws Exception {
        // Arrange
        long nonExistentId = 99L;
        // Configura o mock para lançar a exceção quando o ID não existe
        doThrow(new EntityNotFoundException("Feature não encontrada")).when(featureService).delete(nonExistentId);

        // Act & Assert
        mockMvc.perform(delete("/api/features/{id}", nonExistentId)
                        .with(csrf()))
                .andExpect(status().isNotFound());
    }
}