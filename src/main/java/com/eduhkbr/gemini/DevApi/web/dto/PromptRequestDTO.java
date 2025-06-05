package com.eduhkbr.gemini.DevApi.web.dto;

import jakarta.validation.constraints.NotBlank;

public class PromptRequestDTO {
    @NotBlank(message = "Profissão é obrigatória")
    private String profession;
    @NotBlank(message = "Funcionalidade é obrigatória")
    private String feature;
    @NotBlank(message = "Descrição é obrigatória")
    private String description;

    public String getProfession() { return profession; }
    public void setProfession(String profession) { this.profession = profession; }
    public String getFeature() { return feature; }
    public void setFeature(String feature) { this.feature = feature; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
