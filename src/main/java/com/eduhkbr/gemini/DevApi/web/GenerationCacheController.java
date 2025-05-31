package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.GenerationCache;
import com.eduhkbr.gemini.DevApi.repository.GenerationCacheRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Endpoint para consultar o histórico de resultados gerados pela IA (cache persistido).
 */
@RestController
@RequestMapping("/v1/cache")
public class GenerationCacheController {
    private final GenerationCacheRepository repository;
    public GenerationCacheController(GenerationCacheRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<GenerationCache> listAll() {
        return repository.findAll();
    }
}
