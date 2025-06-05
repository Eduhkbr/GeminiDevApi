package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.web.dto.JavaClassDTO;
import com.eduhkbr.gemini.DevApi.model.GenerationResult;
import com.eduhkbr.gemini.DevApi.model.JavaClass;
import com.eduhkbr.gemini.DevApi.service.JavaClassAnalyzerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * Controller REST para análise de classes Java.
 * Recebe uma lista de DTOs e retorna documentação e testes gerados.
 */
@RestController
@RequestMapping("/v1/analyze")
public class GenerationController {

  private final JavaClassAnalyzerService analyzer;

  public GenerationController(JavaClassAnalyzerService analyzer) {
    this.analyzer = analyzer;
  }

  /**
   * Analisa uma classe Java e retorna o resultado.
   * @param dto DTO de classe Java
   * @return resultado de geração
   */
  @PostMapping
  public ResponseEntity<GenerationResult> analyze(@Valid @RequestBody JavaClassDTO dto) {
    if(dto == null || dto.getName() == null || dto.getSourceCode() == null) {
      return ResponseEntity.badRequest().build();
    }
    JavaClass javaClass = new JavaClass(dto.getName(), dto.getSourceCode().replaceAll("[\n\r]", "_"));
    GenerationResult result = analyzer.analyze(javaClass);
    return ResponseEntity.ok(result);
  }
}
