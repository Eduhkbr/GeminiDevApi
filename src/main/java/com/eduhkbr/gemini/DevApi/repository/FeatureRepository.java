package com.eduhkbr.gemini.DevApi.repository;

import com.eduhkbr.gemini.DevApi.model.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FeatureRepository extends JpaRepository<Feature, Long> {
}
