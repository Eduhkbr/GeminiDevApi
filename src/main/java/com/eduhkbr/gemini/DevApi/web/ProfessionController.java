package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.Profession;
import com.eduhkbr.gemini.DevApi.repository.ProfessionRepository;
import com.eduhkbr.gemini.DevApi.web.dto.ProfessionDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/professions")
public class ProfessionController {
    private final ProfessionRepository professionRepository;

    public ProfessionController(ProfessionRepository professionRepository) {
        this.professionRepository = professionRepository;
    }

    @GetMapping
    public List<Profession> getAll() {
        return professionRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Profession> getById(@PathVariable Long id) {
        return professionRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody ProfessionDTO dto) {
        String safeName = dto.getName().replaceAll("[<>\"'\\\\]", "");
        if (safeName.isBlank()) return ResponseEntity.badRequest().body("Nome inválido");
        Profession profession = new Profession();
        profession.setName(safeName);
        Profession saved = professionRepository.save(profession);
        Long id = saved.getId() != null ? saved.getId() : -1L;
        String name = saved.getName() != null ? saved.getName() : "";
        return ResponseEntity.ok(Map.of("id", id, "name", name));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @Valid @RequestBody ProfessionDTO dto) {
        return professionRepository.findById(id)
                .map(existing -> {
                    String safeName = dto.getName().replaceAll("[<>\"'\\\\]", "");
                    if (safeName.isBlank()) return ResponseEntity.badRequest().body("Nome inválido");
                    existing.setName(safeName);
                    Profession saved = professionRepository.save(existing);
                    Long retId = saved.getId() != null ? saved.getId() : -1L;
                    String retName = saved.getName() != null ? saved.getName() : "";
                    return ResponseEntity.ok(Map.of("id", retId, "name", retName));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        if (!professionRepository.existsById(id)) return ResponseEntity.notFound().build();
        professionRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
