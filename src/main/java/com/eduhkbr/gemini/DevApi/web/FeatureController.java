package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.Feature;
import com.eduhkbr.gemini.DevApi.service.FeatureService;
import com.eduhkbr.gemini.DevApi.web.dto.FeatureRequestDTO;
import com.eduhkbr.gemini.DevApi.web.dto.FeatureResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/features")
public class FeatureController {

    private final FeatureService featureService;

    public FeatureController(FeatureService featureService) {
        this.featureService = featureService;
    }

    @GetMapping
    public ResponseEntity<List<FeatureResponseDTO>> getAll() {
        List<FeatureResponseDTO> features = featureService.findAll().stream()
                .map(FeatureResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(features);
    }

    @PostMapping
    public ResponseEntity<FeatureResponseDTO> create(@Valid @RequestBody FeatureRequestDTO dto) {
        Feature savedFeature = featureService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(FeatureResponseDTO.fromEntity(savedFeature));
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<FeatureResponseDTO> update(@PathVariable Long id, @Valid @RequestBody FeatureRequestDTO dto) {
        Feature updatedFeature = featureService.update(id, dto);
        return ResponseEntity.ok(FeatureResponseDTO.fromEntity(updatedFeature));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        featureService.delete(id);
        return ResponseEntity.noContent().build();
    }
}