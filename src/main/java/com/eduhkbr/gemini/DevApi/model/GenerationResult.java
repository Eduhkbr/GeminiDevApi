package com.eduhkbr.gemini.DevApi.model;

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
