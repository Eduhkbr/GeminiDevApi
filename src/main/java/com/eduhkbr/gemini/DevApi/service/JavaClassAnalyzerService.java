package com.eduhkbr.gemini.DevApi.service;

import com.eduhkbr.gemini.DevApi.llm.LlmClient;
import com.eduhkbr.gemini.DevApi.model.GenerationResult;
import com.eduhkbr.gemini.DevApi.model.JavaClass;
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
    String promptDoc  = String.format(docTemplate, javaClass.getName(), javaClass.getSourceCode());
    String documentation = llm.sendPrompt(promptDoc);

    String promptTest = String.format(testTemplate, javaClass.getName(), javaClass.getSourceCode());
    String tests = llm.sendPrompt(promptTest);

    return new GenerationResult(documentation, tests);
  }
}
