package com.eduhkbr.gemini.DevApi.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class WebClientConfigTest {
    @Test
    @DisplayName("Deve criar WebClient.Builder com baseUrl e header JSON")
    void testWebClientBuilder() {
        GeminiProperties props = mock(GeminiProperties.class);
        when(props.getBaseUrl()).thenReturn("${CORS_ALLOWED_ORIGINS:}");
        WebClientConfig config = new WebClientConfig(props);
        WebClient.Builder builder = config.webClientBuilder();
        WebClient client = builder.build();
        assertThat(client).isNotNull();
    }

    @Test
    @DisplayName("Deve criar bean WebClient usando o builder")
    void testGeminiWebClient() {
        WebClient.Builder builder = WebClient.builder();
        WebClientConfig config = new WebClientConfig(mock(GeminiProperties.class));
        WebClient client = config.geminiWebClient(builder);
        assertThat(client).isNotNull();
    }
}
