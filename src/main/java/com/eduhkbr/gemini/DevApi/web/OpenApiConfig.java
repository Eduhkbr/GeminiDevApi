package com.eduhkbr.gemini.DevApi.web;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
                        .description("API para análise e documentação automática de classes Java usando LLM (Gemini)."));
    }
}
