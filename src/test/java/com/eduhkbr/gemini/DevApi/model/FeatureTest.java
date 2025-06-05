package com.eduhkbr.gemini.DevApi.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FeatureTest {
    @Test
    void gettersESetters() {
        Feature feature = new Feature();
        feature.setId(10L);
        feature.setName("Login");
        Profession prof = new Profession();
        prof.setName("Dev");
        feature.setProfession(prof);
        assertEquals(10L, feature.getId());
        assertEquals("Login", feature.getName());
        assertEquals("Dev", feature.getProfession().getName());
    }
}
