package com.eduhkbr.gemini.DevApi.model;

/**
 * Representa uma classe Java a ser analisada.
 * Contém o nome e o código-fonte da classe.
 */
public class JavaClass {
  private final String name;
  private final String sourceCode;

  public JavaClass(String name, String sourceCode) {
    this.name = name;
    this.sourceCode = sourceCode;
  }
  public String getName() { return name; }
  public String getSourceCode() { return sourceCode; }
}
