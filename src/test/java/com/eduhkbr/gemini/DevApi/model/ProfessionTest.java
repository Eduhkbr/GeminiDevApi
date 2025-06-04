package com.eduhkbr.gemini.DevApi.model;

import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProfessionTest {
    @Test
    void gettersESetters() {
        Profession prof = new Profession();
        prof.setId(1L);
        prof.setName("Engenheiro");
        Feature f1 = new Feature();
        f1.setName("Func1");
        Feature f2 = new Feature();
        f2.setName("Func2");
        prof.setFeatures(List.of(f1, f2));
        assertEquals(1L, prof.getId());
        assertEquals("Engenheiro", prof.getName());
        assertEquals(2, prof.getFeatures().size());
        assertEquals("Func1", prof.getFeatures().get(0).getName());
    }
}
