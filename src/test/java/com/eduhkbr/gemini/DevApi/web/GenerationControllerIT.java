package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.web.dto.JavaClassDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@SpringBootTest
@AutoConfigureMockMvc
class GenerationControllerIT {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void analyze_DeveRetornar200EListaDeResultados() throws Exception {
        JavaClassDTO dto = new JavaClassDTO();
        dto.setName("OrderService");
        dto.setSourceCode("public class OrderService { }");
        String json = objectMapper.writeValueAsString(List.of(dto));

        mockMvc.perform(post("/v1/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].documentation").exists());
    }
}
