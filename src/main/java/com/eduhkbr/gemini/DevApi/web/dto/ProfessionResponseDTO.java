package com.eduhkbr.gemini.DevApi.web.dto;

import com.eduhkbr.gemini.DevApi.model.Profession;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public record ProfessionResponseDTO(Long id, String name, List<FeatureInfoDTO> features) {

    public record FeatureInfoDTO(Long id, String name) {}

    public static ProfessionResponseDTO fromEntity(Profession profession) {
        List<FeatureInfoDTO> featureInfos;

        if (profession.getFeatures() != null && !profession.getFeatures().isEmpty()) {
            featureInfos = profession.getFeatures().stream()
                    .map(feature -> new FeatureInfoDTO(feature.getId(), feature.getName()))
                    .collect(Collectors.toList());
        } else {
            featureInfos = Collections.emptyList();
        }

        return new ProfessionResponseDTO(profession.getId(), profession.getName(), featureInfos);
    }
}