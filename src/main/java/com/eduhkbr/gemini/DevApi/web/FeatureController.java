package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.Feature;
import com.eduhkbr.gemini.DevApi.repository.FeatureRepository;
import com.eduhkbr.gemini.DevApi.web.dto.FeatureDTO;
import com.eduhkbr.gemini.DevApi.model.Profession;
import com.eduhkbr.gemini.DevApi.repository.ProfessionRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/features")
public class FeatureController {
    private final FeatureRepository featureRepository;
    private final ProfessionRepository professionRepository;
    public FeatureController(FeatureRepository featureRepository, ProfessionRepository professionRepository) {
        this.featureRepository = featureRepository;
        this.professionRepository = professionRepository;
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
    public ResponseEntity<?> create(@Valid @RequestBody FeatureDTO dto) {
        String safeName = dto.getName().replaceAll("[<>\"'\\\\]", "");
        if (safeName.isBlank()) return ResponseEntity.badRequest().body("Nome inválido");
        Profession profession = null;
        if (dto.getProfessionId() != null) {
            profession = professionRepository.findById(dto.getProfessionId()).orElse(null);
            if (profession == null) return ResponseEntity.badRequest().body("Profissão não encontrada");
        }
        Feature feature = new Feature();
        feature.setName(safeName);
        feature.setProfession(profession);
        Feature saved = featureRepository.save(feature);
        Long id = saved.getId() != null ? saved.getId() : -1L;
        String name = saved.getName() != null ? saved.getName() : "";
        return ResponseEntity.ok(Map.of("id", id, "name", name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody FeatureDTO dto) {
        return featureRepository.findById(id)
                .map(existing -> {
                    String safeName = dto.getName().replaceAll("[<>\"'\\\\]", "");
                    if (safeName.isBlank()) return ResponseEntity.badRequest().body("Nome inválido");
                    Profession profession = null;
                    if (dto.getProfessionId() != null) {
                        profession = professionRepository.findById(dto.getProfessionId()).orElse(null);
                        if (profession == null) return ResponseEntity.badRequest().body("Profissão não encontrada");
                    }
                    existing.setName(safeName);
                    existing.setProfession(profession);
                    Feature saved = featureRepository.save(existing);
                    Long retId = saved.getId() != null ? saved.getId() : -1L;
                    String retName = saved.getName() != null ? saved.getName() : "";
                    return ResponseEntity.ok(Map.of("id", retId, "name", retName));
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
