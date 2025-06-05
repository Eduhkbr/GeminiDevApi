package com.eduhkbr.gemini.DevApi.service;

import com.eduhkbr.gemini.DevApi.llm.LlmClient;
import com.eduhkbr.gemini.DevApi.model.GenerationResult;
import com.eduhkbr.gemini.DevApi.model.JavaClass;
import com.eduhkbr.gemini.DevApi.model.GenerationCache;
import com.eduhkbr.gemini.DevApi.repository.GenerationCacheRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import java.util.MissingFormatArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por analisar classes Java e gerar documentação e testes automáticos
 * utilizando LLM (Large Language Model).
 * <p>
 * Aplica templates configuráveis para gerar prompts e processa as respostas do modelo.
 * </p>
 */
@Service
public class JavaClassAnalyzerService {

  private static final Logger logger = LoggerFactory.getLogger(JavaClassAnalyzerService.class);
  private final LlmClient llm;
  private final GenerationCacheRepository cacheRepository;
  @Value("${analysis.prompt.documentation}")
  private String docTemplate;
  @Value("${analysis.prompt.tests}")
  private String testTemplate;
  private final Counter cacheHitCounter;
  private final Counter iaCallCounter;

  /**
   * Construtor com injeção de dependência do client LLM.
   * @param llm client para comunicação com modelo generativo
   * @param cacheRepository repositório para cache de gerações
   */
  public JavaClassAnalyzerService(LlmClient llm, GenerationCacheRepository cacheRepository, MeterRegistry meterRegistry) {
    this.llm = llm;
    this.cacheRepository = cacheRepository;
    this.cacheHitCounter = meterRegistry.counter("generation.cache.hits");
    this.iaCallCounter = meterRegistry.counter("generation.ia.calls");
  }

  /**
   * Analisa uma classe Java, utilizando cache e persistência para evitar chamadas desnecessárias à IA.
   */
  @Cacheable(value = "generationResult", key = "#javaClass.name + '-' + T(org.apache.commons.codec.digest.DigestUtils).sha256Hex(#javaClass.sourceCode)")
  @Transactional
  public GenerationResult analyze(JavaClass javaClass) {
    String key = javaClass.getName() + "-" + org.apache.commons.codec.digest.DigestUtils.sha256Hex(javaClass.getSourceCode());
    // 1. Busca no banco
    GenerationCache cached = cacheRepository.findByHash(key).orElse(null);
    if (cached != null) {
      cacheHitCounter.increment();
      return new GenerationResult(cached.getResult(), null); // Ajuste conforme estrutura
    }
    // 2. Chama IA
    iaCallCounter.increment();
    logger.info("Analisando classe do hash: {} (cache miss)", key);
    String promptDoc = null;
    String documentation = null;
    String promptTest = null;
    String tests = null;
    try {
      logger.info("Template de documentação: {}", docTemplate);
      promptDoc = String.format(docTemplate, javaClass.getName(), javaClass.getSourceCode());
      documentation = llm.sendPrompt(promptDoc);
    } catch (MissingFormatArgumentException e) {
      logger.error("Erro de formatação no template de documentação: {} | Template: {} | Args: nome={}, sourceCode={}", e.getMessage(), docTemplate, javaClass.getName(), javaClass.getSourceCode(), e);
      documentation = "[ERRO] Falha ao gerar documentação: template inválido ou argumentos insuficientes.";
    } catch (Exception e) {
      logger.error("Erro inesperado ao gerar documentação: {}", e.getMessage(), e);
      documentation = null;
    }
    try {
      logger.info("Template de testes: {}", testTemplate);
      logger.info("Argumentos: nome={} (sourceCode omitido por segurança)", javaClass.getName());
      promptTest = String.format(testTemplate, javaClass.getName(), javaClass.getSourceCode());
      tests = llm.sendPrompt(promptTest);
    } catch (MissingFormatArgumentException e) {
      logger.error("Erro de formatação no template de testes: {} | Template: {} | Args: nome={}, sourceCode={}", e.getMessage(), testTemplate, javaClass.getName(), javaClass.getSourceCode(), e);
      tests = "[ERRO] Falha ao gerar testes: template inválido ou argumentos insuficientes.";
    } catch (Exception e) {
      logger.error("Erro inesperado ao gerar testes: {}", e.getMessage(), e);
      tests = null;
    }
    GenerationResult result = new GenerationResult(
      documentation != null ? documentation : "[ERRO] Falha ao gerar documentação: resultado nulo.",
      tests != null ? tests : "[ERRO] Falha ao gerar testes: resultado nulo."
    );
    // 3. Salva no banco
    GenerationCache entity = new GenerationCache();
    entity.setHash(key);
    entity.setName(javaClass.getName());
    entity.setSourceCode(javaClass.getSourceCode());
    // Garante que result nunca será null
    entity.setResult(documentation != null ? documentation : "N/A");
    cacheRepository.save(entity);
    return result;
  }
}
