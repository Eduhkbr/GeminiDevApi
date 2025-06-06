package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.Profession;
import com.eduhkbr.gemini.DevApi.service.ProfessionService;
import com.eduhkbr.gemini.DevApi.web.dto.ProfessionRequestDTO;
import com.eduhkbr.gemini.DevApi.web.dto.ProfessionResponseDTO;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/professions")
public class ProfessionController {
    
    private final ProfessionService professionService;

    public ProfessionController(ProfessionService professionService) {
        this.professionService = professionService;
    }

    @GetMapping
    public ResponseEntity<List<ProfessionResponseDTO>> getAll() {
        List<ProfessionResponseDTO> professions = professionService.findAll().stream()
                .map(ProfessionResponseDTO::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(professions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessionResponseDTO> getById(@PathVariable Long id) {
        Profession profession = professionService.findById(id);
        return ResponseEntity.ok(ProfessionResponseDTO.fromEntity(profession));
    }

    @PostMapping
    public ResponseEntity<ProfessionResponseDTO> create(@Valid @RequestBody ProfessionRequestDTO dto) {
        Profession savedProfession = professionService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ProfessionResponseDTO.fromEntity(savedProfession));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfessionResponseDTO> update(@PathVariable Long id, @Valid @RequestBody ProfessionRequestDTO dto) {
        Profession updatedProfession = professionService.update(id, dto);
        return ResponseEntity.ok(ProfessionResponseDTO.fromEntity(updatedProfession));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        professionService.delete(id);
        return ResponseEntity.noContent().build();
    }
}