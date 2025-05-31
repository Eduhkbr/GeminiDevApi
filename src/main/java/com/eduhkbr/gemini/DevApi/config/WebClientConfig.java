package com.eduhkbr.gemini.DevApi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
    private final GeminiProperties props;

    public WebClientConfig(GeminiProperties props) {
        this.props = props;
    }

    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder()
            .baseUrl(props.getBaseUrl())
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.getKey())
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);
    }

    @Bean
    public WebClient geminiWebClient(WebClient.Builder builder) {
        return builder.build();
    }
}
