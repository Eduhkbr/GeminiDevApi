package com.eduhkbr.gemini.DevApi.model;

import jakarta.persistence.*;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;

@Entity
public class Profession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @OneToMany(mappedBy = "profession", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Feature> features;

    // Getters e setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public List<Feature> getFeatures() { return features; }
    public void setFeatures(List<Feature> features) { this.features = features; }
}
