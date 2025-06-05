package com.eduhkbr.gemini.DevApi.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import java.util.List;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("DevApi - API de Análise de Classes Java")
                        .version("1.0")
                        .description("API profissional para análise e documentação automática de classes Java usando LLM (Gemini).\n\n" +
                                "## Funcionalidades\n" +
                                "- Geração de documentação automática\n" +
                                "- Geração de esqueleto de testes unitários\n" +
                                "- Integração com IA Gemini\n\n" +
                                "## Como usar\n" +
                                "1. Faça login e obtenha um token JWT.\n" +
                                "2. Use o endpoint `/v1/analyze` ou `/v1/prompt-analyze` para enviar sua requisição.\n" +
                                "3. Consulte o Swagger UI para exemplos e schemas.\n")
                        .contact(new Contact()
                                .name("Equipe DevApi")
                                .email("contato@devapi.com")
                                .url("https://devapi.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT"))
                        .termsOfService("https://devapi.com/termos"))
                .servers(List.of(
                        new Server().url("http://localhost:8080").description("Servidor Local"),
                        new Server().url("https://devapi-service-940965750406.us-central1.run.app").description("Servidor Produção")
                ));
    }
}
