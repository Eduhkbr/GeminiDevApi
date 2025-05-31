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
    GenerateTextRequest body = new GenerateTextRequest(
      props.getModelName(),
      new Prompt(prompt),
      512,
      0.2
    );

    Mono<GenerateTextResponse> resp = client.post()
      .uri("/models/{model}:generateText", props.getModelName())
      .bodyValue(body)
      .retrieve()
      .bodyToMono(GenerateTextResponse.class);

    GenerateTextResponse r = resp.block();
    if (r != null && !r.candidates().isEmpty()) {
      logger.info("Resposta recebida do Gemini: {}", r.candidates().get(0).output());
      return r.candidates().get(0).output();
    }
    logger.warn("Nenhuma resposta recebida do Gemini");
    return null;
  }
}

record Prompt(String text) {}
record GenerateTextRequest(String model, Prompt prompt, int maxOutputTokens, double temperature) {}
record Candidate(String output) {}
record GenerateTextResponse(List<Candidate> candidates) {}
