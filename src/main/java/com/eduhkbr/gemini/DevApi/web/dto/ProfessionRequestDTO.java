package com.eduhkbr.gemini.DevApi.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ProfessionRequestDTO {
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String name;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}