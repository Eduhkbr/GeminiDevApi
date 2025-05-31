package com.eduhkbr.gemini.DevApi.web.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO para transferência de dados de classes Java.
 * Utilizado como entrada nos endpoints REST.
 */
public class JavaClassDTO {
    @NotBlank(message = "O nome da classe não pode ser vazio")
    private String name;
    @NotBlank(message = "O código-fonte não pode ser vazio")
    private String sourceCode;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSourceCode() { return sourceCode; }
    public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }
}
