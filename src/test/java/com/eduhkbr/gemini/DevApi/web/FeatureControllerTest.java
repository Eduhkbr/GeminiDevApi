package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.Feature;
import com.eduhkbr.gemini.DevApi.model.Profession;
import com.eduhkbr.gemini.DevApi.service.FeatureService;
import com.eduhkbr.gemini.DevApi.util.JwtUtil;
import com.eduhkbr.gemini.DevApi.web.dto.FeatureRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

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

    @Test
    @WithMockUser(roles = "ADMIN")
    void create_withValidData_shouldReturn201Created() throws Exception {
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

        mockMvc.perform(post("/api/features")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dtoWithBlankName)))
                .andExpect(status().isBadRequest());
    }
}