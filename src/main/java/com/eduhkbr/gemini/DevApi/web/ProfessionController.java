package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.Profession;
import com.eduhkbr.gemini.DevApi.repository.ProfessionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

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
    public Profession create(@RequestBody Profession profession) {
        return professionRepository.save(profession);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Profession> update(@PathVariable Long id, @RequestBody Profession profession) {
        return professionRepository.findById(id)
                .map(existing -> {
                    existing.setName(profession.getName());
                    return ResponseEntity.ok(professionRepository.save(existing));
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
