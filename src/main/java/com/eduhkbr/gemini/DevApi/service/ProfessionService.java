package com.eduhkbr.gemini.DevApi.service;

import com.eduhkbr.gemini.DevApi.model.Profession;
import com.eduhkbr.gemini.DevApi.repository.ProfessionRepository;
import com.eduhkbr.gemini.DevApi.web.dto.ProfessionRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProfessionService {

    @Autowired
    private ProfessionRepository professionRepository;

    @Transactional(readOnly = true)
    public List<Profession> findAll() {
        return professionRepository.findAllWithFeatures();
    }
    
    @Transactional(readOnly = true)
    public Profession findById(Long id) {
        return professionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Profissão não encontrada com o ID: " + id));
    }

    @Transactional
    public Profession create(ProfessionRequestDTO dto) {
        String safeName = sanitize(dto.getName());
        Profession profession = new Profession();
        profession.setName(safeName);
        return professionRepository.save(profession);
    }

    @Transactional
    public Profession update(Long id, ProfessionRequestDTO dto) {
        Profession existingProfession = findById(id); // Reutiliza o método findById
        String safeName = sanitize(dto.getName());
        existingProfession.setName(safeName);
        return professionRepository.save(existingProfession);
    }

    @Transactional
    public void delete(Long id) {
        if (!professionRepository.existsById(id)) {
            throw new EntityNotFoundException("Profissão não encontrada com o ID: " + id);
        }
        professionRepository.deleteById(id);
    }

    private String sanitize(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("O nome não pode ser vazio.");
        }
        return text.replaceAll("[<>\"'\\\\]", "");
    }
}