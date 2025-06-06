package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.Feature;
import com.eduhkbr.gemini.DevApi.model.Profession;
import com.eduhkbr.gemini.DevApi.service.ProfessionService;
import com.eduhkbr.gemini.DevApi.web.dto.ProfessionRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProfessionController.class)
class ProfessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProfessionService professionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getAll_shouldReturnListOfProfessionsWithFeatures() throws Exception {
        Profession profession = new Profession();
        profession.setId(1L);
        profession.setName("Engenheiro de QA");
        
        Feature feature = new Feature();
        feature.setId(201L);
        feature.setName("Escrever testes automatizados");
        
        profession.setFeatures(List.of(feature));
        given(professionService.findAll()).willReturn(List.of(profession));
        
        mockMvc.perform(get("/api/professions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("Engenheiro de QA"))
                .andExpect(jsonPath("$[0].features").isArray())
                .andExpect(jsonPath("$[0].features.length()").value(1))
                .andExpect(jsonPath("$[0].features[0].id").value(201L))
                .andExpect(jsonPath("$[0].features[0].name").value("Escrever testes automatizados"));
    }

    @Test
    void getAll_shouldReturnListOfProfessions() throws Exception {
        Profession profession = new Profession();
        profession.setId(1L);
        profession.setName("DBA");
        
        given(professionService.findAll()).willReturn(Collections.singletonList(profession));
        
        mockMvc.perform(get("/api/professions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("DBA"));
    }
    
    @Test
    void create_shouldReturn201Created() throws Exception {
        ProfessionRequestDTO dto = new ProfessionRequestDTO();
        dto.setName("Engenheiro de Dados");
        
        Profession savedProfession = new Profession();
        savedProfession.setId(1L);
        savedProfession.setName("Engenheiro de Dados");
        
        given(professionService.create(any(ProfessionRequestDTO.class))).willReturn(savedProfession);
        
        mockMvc.perform(post("/api/professions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Engenheiro de Dados"));
    }
}