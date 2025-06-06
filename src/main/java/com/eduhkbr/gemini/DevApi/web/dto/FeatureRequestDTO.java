package com.eduhkbr.gemini.DevApi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FeatureRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String name;
    private Long professionId;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Long getProfessionId() { return professionId; }
    public void setProfessionId(Long professionId) { this.professionId = professionId; }
}