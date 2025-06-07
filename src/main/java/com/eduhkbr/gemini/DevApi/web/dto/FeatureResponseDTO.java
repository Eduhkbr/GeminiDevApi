package com.eduhkbr.gemini.DevApi.web.dto;

import com.eduhkbr.gemini.DevApi.model.Feature;

public record FeatureResponseDTO(Long id, String name, ProfessionInfoDTO profession) {

    public record ProfessionInfoDTO(Long id, String name) {}

    public static FeatureResponseDTO fromEntity(Feature feature) {
        ProfessionInfoDTO professionInfo = (feature.getProfession() != null) ?
                new ProfessionInfoDTO(feature.getProfession().getId(), feature.getProfession().getName()) :
                null;
        return new FeatureResponseDTO(feature.getId(), feature.getName(), professionInfo);
    }
}