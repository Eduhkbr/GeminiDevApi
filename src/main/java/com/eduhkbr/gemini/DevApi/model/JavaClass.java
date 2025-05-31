package com.eduhkbr.gemini.DevApi.model;

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
