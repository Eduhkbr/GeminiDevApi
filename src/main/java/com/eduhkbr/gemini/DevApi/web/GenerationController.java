package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.web.dto.JavaClassDTO;
import com.eduhkbr.gemini.DevApi.model.GenerationResult;
import com.eduhkbr.gemini.DevApi.model.JavaClass;
import com.eduhkbr.gemini.DevApi.service.JavaClassAnalyzerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.Map;

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
  public ResponseEntity<?> analyze(@Valid @RequestBody JavaClassDTO dto) {
    if (dto == null || dto.getName() == null || dto.getSourceCode() == null) {
      return ResponseEntity.badRequest().body(Map.of("error", "Dados obrigatórios ausentes"));
    }
    // Validação extra: limita tamanho do código e nome, impede caracteres suspeitos
    if (dto.getName().length() > 100 || dto.getSourceCode().length() > 20000) {
      return ResponseEntity.badRequest().body(Map.of("error", "Nome ou código muito longo"));
    }
    if (!dto.getName().matches("^[\\w.\\- ]+$")) {
      return ResponseEntity.badRequest().body(Map.of("error", "Nome inválido"));
    }
    // Remove caracteres de controle e normaliza o código
    String safeName = dto.getName().replaceAll("[<>\"'\\\\]", "");
    String safeSource = dto.getSourceCode().replaceAll("[<>\"'\\\\]", "").replaceAll("[\\p{Cntrl}&&[^\n\t]]", "");
    JavaClass javaClass = new JavaClass(safeName, safeSource);
    GenerationResult result = analyzer.analyze(javaClass);
    return ResponseEntity.ok(result);
  }
}
