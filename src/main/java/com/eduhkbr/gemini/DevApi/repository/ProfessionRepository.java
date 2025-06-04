package com.eduhkbr.gemini.DevApi.repository;

import com.eduhkbr.gemini.DevApi.model.Profession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfessionRepository extends JpaRepository<Profession, Long> {
}
