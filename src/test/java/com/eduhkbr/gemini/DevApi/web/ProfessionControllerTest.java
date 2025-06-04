package com.eduhkbr.gemini.DevApi.web;

import com.eduhkbr.gemini.DevApi.model.Profession;
import com.eduhkbr.gemini.DevApi.repository.ProfessionRepository;
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

class ProfessionControllerTest {
    @Mock
    private ProfessionRepository professionRepository;

    @InjectMocks
    private ProfessionController professionController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        Profession p1 = new Profession();
        Profession p2 = new Profession();
        when(professionRepository.findAll()).thenReturn(Arrays.asList(p1, p2));
        List<Profession> result = professionController.getAll();
        assertEquals(2, result.size());
    }

    @Test
    void testGetByIdFound() {
        Profession p = new Profession();
        when(professionRepository.findById(1L)).thenReturn(Optional.of(p));
        ResponseEntity<Profession> response = professionController.getById(1L);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(p, response.getBody());
    }

    @Test
    void testGetByIdNotFound() {
        when(professionRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Profession> response = professionController.getById(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testCreate() {
        Profession p = new Profession();
        when(professionRepository.save(p)).thenReturn(p);
        Profession result = professionController.create(p);
        assertEquals(p, result);
    }

    @Test
    void testUpdateFound() {
        Profession existing = new Profession();
        existing.setName("Old");
        Profession update = new Profession();
        update.setName("New");
        when(professionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(professionRepository.save(existing)).thenReturn(existing);
        ResponseEntity<Profession> response = professionController.update(1L, update);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("New", response.getBody().getName());
    }

    @Test
    void testUpdateNotFound() {
        Profession update = new Profession();
        when(professionRepository.findById(1L)).thenReturn(Optional.empty());
        ResponseEntity<Profession> response = professionController.update(1L, update);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void testDeleteFound() {
        when(professionRepository.existsById(1L)).thenReturn(true);
        doNothing().when(professionRepository).deleteById(1L);
        ResponseEntity<Void> response = professionController.delete(1L);
        assertEquals(204, response.getStatusCodeValue());
    }

    @Test
    void testDeleteNotFound() {
        when(professionRepository.existsById(1L)).thenReturn(false);
        ResponseEntity<Void> response = professionController.delete(1L);
        assertEquals(404, response.getStatusCodeValue());
    }
}
