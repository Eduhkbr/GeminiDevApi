package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.Feature;
import com.eduhkbr.gemini.DevApi.repository.FeatureRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/features")
public class FeatureController {
    private final FeatureRepository featureRepository;
    public FeatureController(FeatureRepository featureRepository) {
        this.featureRepository = featureRepository;
    }

    @GetMapping
    public List<Feature> getAll() {
        return featureRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Feature> getById(@PathVariable Long id) {
        return featureRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Feature create(@RequestBody Feature feature) {
        return featureRepository.save(feature);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Feature> update(@PathVariable Long id, @RequestBody Feature feature) {
        return featureRepository.findById(id)
                .map(existing -> {
                    existing.setName(feature.getName());
                    return ResponseEntity.ok(featureRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!featureRepository.existsById(id)) return ResponseEntity.notFound().build();
        featureRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
