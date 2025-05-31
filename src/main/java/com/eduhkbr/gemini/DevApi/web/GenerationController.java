package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.web.dto.JavaClassDTO;
import com.eduhkbr.gemini.DevApi.model.GenerationResult;
import com.eduhkbr.gemini.DevApi.model.JavaClass;
import com.eduhkbr.gemini.DevApi.service.JavaClassAnalyzerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller responsável por analisar classes Java e retornar resultados de geração.
 */
@RestController
@RequestMapping("/v1/analyze")
public class GenerationController {

  private final JavaClassAnalyzerService analyzer;

  public GenerationController(JavaClassAnalyzerService analyzer) {
    this.analyzer = analyzer;
  }

  /**
   * Analisa uma lista de classes Java e retorna os resultados.
   * @param classes lista de DTOs de classes Java
   * @return lista de resultados de geração
   */
  @PostMapping
  public ResponseEntity<List<GenerationResult>> analyze(@Valid @RequestBody List<JavaClassDTO> classes) {
    var results = classes.stream()
      .map(dto -> new JavaClass(dto.getName(), dto.getSourceCode()))
      .map(analyzer::analyze)
      .collect(Collectors.toList());
    return ResponseEntity.ok(results);
  }
}
