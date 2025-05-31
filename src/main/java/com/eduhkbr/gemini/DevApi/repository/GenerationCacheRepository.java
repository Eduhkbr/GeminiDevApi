package com.eduhkbr.gemini.DevApi.repository;

import com.eduhkbr.gemini.DevApi.model.GenerationCache;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

/**
 * Repositório JPA para acesso ao cache de geração automática.
 */
public interface GenerationCacheRepository extends JpaRepository<GenerationCache, Long> {
    Optional<GenerationCache> findByHash(String hash);
}
