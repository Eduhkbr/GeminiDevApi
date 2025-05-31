package com.eduhkbr.gemini.DevApi.service;

import com.eduhkbr.gemini.DevApi.llm.LlmClient;
import com.eduhkbr.gemini.DevApi.model.GenerationResult;
import com.eduhkbr.gemini.DevApi.model.JavaClass;
import java.util.MissingFormatArgumentException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Serviço responsável por analisar classes Java.
 */
@Service
public class JavaClassAnalyzerService {

  private static final Logger logger = LoggerFactory.getLogger(JavaClassAnalyzerService.class);
  private final LlmClient llm;
  @Value("${analysis.prompt.documentation}")
  private String docTemplate;
  @Value("${analysis.prompt.tests}")
  private String testTemplate;

  public JavaClassAnalyzerService(LlmClient llm) {
    this.llm = llm;
  }

  /**
   * Analisa uma classe Java e retorna o resultado da geração.
   * @param javaClass classe Java a ser analisada
   * @return resultado da geração
   */
  public GenerationResult analyze(JavaClass javaClass) {
    logger.info("Analisando classe: {}", javaClass.getName());
    String promptDoc = null;
    String documentation = null;
    String promptTest = null;
    String tests = null;
    try {
      logger.info("Template de documentação: {}", docTemplate);
      logger.info("Argumentos: nome={}, sourceCode={} ", javaClass.getName(), javaClass.getSourceCode());
      promptDoc = String.format(docTemplate, javaClass.getName(), javaClass.getSourceCode());
      documentation = llm.sendPrompt(promptDoc);
    } catch (MissingFormatArgumentException e) {
      logger.error("Erro de formatação no template de documentação: {} | Template: {} | Args: nome={}, sourceCode={}", e.getMessage(), docTemplate, javaClass.getName(), javaClass.getSourceCode(), e);
      documentation = "[ERRO] Falha ao gerar documentação: template inválido ou argumentos insuficientes.";
    }
    try {
      logger.info("Template de testes: {}", testTemplate);
      logger.info("Argumentos: nome={}, sourceCode={} ", javaClass.getName(), javaClass.getSourceCode());
      promptTest = String.format(testTemplate, javaClass.getName(), javaClass.getSourceCode());
      tests = llm.sendPrompt(promptTest);
    } catch (MissingFormatArgumentException e) {
      logger.error("Erro de formatação no template de testes: {} | Template: {} | Args: nome={}, sourceCode={}", e.getMessage(), testTemplate, javaClass.getName(), javaClass.getSourceCode(), e);
      tests = "[ERRO] Falha ao gerar testes: template inválido ou argumentos insuficientes.";
    }
    return new GenerationResult(documentation, tests);
  }
}
