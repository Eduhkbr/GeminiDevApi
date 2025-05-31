package com.eduhkbr.gemini.DevApi.llm;

import com.eduhkbr.gemini.DevApi.config.GeminiProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.List;

/**
 * Client para comunicação com o modelo Gemini via API HTTP.
 */
@Service
public class GeminiLlmClient implements LlmClient {

  private static final Logger logger = LoggerFactory.getLogger(GeminiLlmClient.class);
  private final WebClient client;
  private final GeminiProperties props;

  public GeminiLlmClient(WebClient geminiWebClient, GeminiProperties props) {
    this.client = geminiWebClient;
    this.props = props;
  }

  @Override
  public String sendPrompt(String prompt) {
    logger.info("Enviando prompt para Gemini: {}", prompt);
    // Monta o corpo conforme Gemini 2.x
    Part part = new Part(prompt);
    Content content = new Content("user", List.of(part));
    GenerateContentRequest body = new GenerateContentRequest(List.of(content));

    Mono<GenerateContentResponse> resp = client.post()
      .uri(uriBuilder -> uriBuilder
        .path("/models/{model}:generateContent")
        .queryParam("key", props.getKey())
        .build(props.getModelName()))
      .bodyValue(body)
      .retrieve()
      .bodyToMono(GenerateContentResponse.class);

    GenerateContentResponse r = resp.block();
    if (r != null && r.candidates() != null && !r.candidates().isEmpty()) {
      List<Part> parts = r.candidates().get(0).content().parts();
      if (parts != null && !parts.isEmpty()) {
        logger.info("Resposta recebida do Gemini: {}", parts.get(0).text());
        return parts.get(0).text();
      }
    }
    logger.warn("Nenhuma resposta recebida do Gemini");
    return null;
  }
}

// Records para Gemini 2.x REST API
record Part(String text) {}
record Content(String role, List<Part> parts) {}
record GenerateContentRequest(List<Content> contents) {}
record Candidate(Content content) {}
record GenerateContentResponse(List<Candidate> candidates) {}
