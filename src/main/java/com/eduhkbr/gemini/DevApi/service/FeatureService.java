package com.eduhkbr.gemini.DevApi.service;

import com.eduhkbr.gemini.DevApi.model.Feature;
import com.eduhkbr.gemini.DevApi.model.Profession;
import com.eduhkbr.gemini.DevApi.repository.FeatureRepository;
import com.eduhkbr.gemini.DevApi.repository.ProfessionRepository;
import com.eduhkbr.gemini.DevApi.web.dto.FeatureRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FeatureService {

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private ProfessionRepository professionRepository;

    @Transactional(readOnly = true)
    public List<Feature> findAll() {
        return featureRepository.findAll();
    }

    @Transactional
    public Feature create(FeatureRequestDTO dto) {
        // A lógica de validação e criação agora vive aqui
        String safeName = sanitize(dto.getName());
        Profession profession = findProfessionById(dto.getProfessionId());

        Feature feature = new Feature();
        feature.setName(safeName);
        feature.setProfession(profession);
        
        return featureRepository.save(feature);
    }

    @Transactional
    public Feature update(Long id, FeatureRequestDTO dto) {
        Feature existingFeature = featureRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Feature não encontrada com o ID: " + id));

        String safeName = sanitize(dto.getName());
        Profession profession = findProfessionById(dto.getProfessionId());

        existingFeature.setName(safeName);
        existingFeature.setProfession(profession);

        return featureRepository.save(existingFeature);
    }

    @Transactional
    public void delete(Long id) {
        if (!featureRepository.existsById(id)) {
            throw new EntityNotFoundException("Feature não encontrada com o ID: " + id);
        }
        featureRepository.deleteById(id);
    }

    private String sanitize(String text) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException("Nome inválido");
        }
        return text.replaceAll("[<>\"'\\\\]", "");
    }

    private Profession findProfessionById(Long professionId) {
        if (professionId == null) {
            return null;
        }
        return professionRepository.findById(professionId)
                .orElseThrow(() -> new EntityNotFoundException("Profissão não encontrada com o ID: " + professionId));
    }
}