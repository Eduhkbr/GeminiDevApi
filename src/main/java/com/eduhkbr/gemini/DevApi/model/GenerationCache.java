package com.eduhkbr.gemini.DevApi.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "generation_cache")
public class GenerationCache {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 128)
    private String hash;

    @Column(nullable = false)
    private String name;

    @Lob
    @Column(nullable = false)
    private String sourceCode;

    @Lob
    @Column(nullable = false)
    private String result;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getSourceCode() { return sourceCode; }
    public void setSourceCode(String sourceCode) { this.sourceCode = sourceCode; }
    public String getResult() { return result; }
    public void setResult(String result) { this.result = result; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
