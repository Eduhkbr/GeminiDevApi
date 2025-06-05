package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.Feature;
import com.eduhkbr.gemini.DevApi.repository.FeatureRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FeatureControllerTest {
    @Mock
    private FeatureRepository featureRepository;

    @InjectMocks
    private FeatureController featureController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        Feature f1 = new Feature();
        Feature f2 = new Feature();
        when(featureRepository.findAll()).thenReturn(Arrays.asList(f1, f2));
        List<Feature> result = featureController.getAll();
        assertEquals(2, result.size());
    }

    @Test
    void testGetByIdFound() {
        Feature f = new Feature();
        when(featureRepository.findById(1L)).thenReturn(Optional.of(f));
        ResponseEntity<Feature> response = featureController.getById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(f, response.getBody());
    }

    @Test
    void testGetByIdNotFound() {
        when(featureRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Feature> response = featureController.getById(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testCreate() {
        Feature f = new Feature();
        when(featureRepository.save(f)).thenReturn(f);
        Feature result = featureController.create(f);
        assertEquals(f, result);
    }

    @Test
    void testUpdateFound() {
        Feature existing = new Feature();
        existing.setName("Old");
        Feature update = new Feature();
        update.setName("New");
        when(featureRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(featureRepository.save(existing)).thenReturn(existing);
        ResponseEntity<Feature> response = featureController.update(1L, update);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("New", response.getBody().getName());
    }

    @Test
    void testUpdateNotFound() {
        Feature update = new Feature();
        when(featureRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Feature> response = featureController.update(1L, update);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testDeleteFound() {
        when(featureRepository.existsById(1L)).thenReturn(true);
        doNothing().when(featureRepository).deleteById(1L);
        ResponseEntity<Void> response = featureController.delete(1L);
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testDeleteNotFound() {
        when(featureRepository.existsById(1L)).thenReturn(false);
        ResponseEntity<Void> response = featureController.delete(1L);
        assertEquals(404, response.getStatusCodeValue());
    }
}
