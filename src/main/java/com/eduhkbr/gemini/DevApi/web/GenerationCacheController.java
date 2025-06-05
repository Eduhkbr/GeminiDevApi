package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.GenerationCache;
import com.eduhkbr.gemini.DevApi.repository.GenerationCacheRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public List<Map<String, Object>> listAll() {
        return repository.findAll().stream()
            .map(cache -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", cache.getId());
                map.put("name", cache.getName());
                map.put("createdAt", cache.getCreatedAt());
                String result = cache.getResult();
                map.put("resultSummary", result != null && result.length() > 100 ? result.substring(0, 100) + "..." : result);
                return map;
            })
            .collect(Collectors.toList());
    }
}
