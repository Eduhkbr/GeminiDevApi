package com.eduhkbr.gemini.DevApi.repository;

import com.eduhkbr.gemini.DevApi.model.Profession;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProfessionRepository extends JpaRepository<Profession, Long> {

    @Query("SELECT p FROM Profession p LEFT JOIN FETCH p.features")
    List<Profession> findAllWithFeatures();
    
}
