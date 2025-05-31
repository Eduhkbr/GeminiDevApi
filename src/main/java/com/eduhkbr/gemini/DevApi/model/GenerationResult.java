package com.eduhkbr.gemini.DevApi.model;

/**
 * Representa o resultado da geração automática de documentação e testes para uma classe Java.
 * Contém o texto da documentação e o esqueleto de testes gerados.
 */
public class GenerationResult {
  private final String documentation;
  private final String testSkeleton;

  public GenerationResult(String documentation, String testSkeleton) {
    this.documentation = documentation;
    this.testSkeleton = testSkeleton;
  }
  public String getDocumentation() { return documentation; }
  public String getTestSkeleton() { return testSkeleton; }
}
