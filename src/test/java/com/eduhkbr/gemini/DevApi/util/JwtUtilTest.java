package com.eduhkbr.gemini.DevApi.util;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {
    private JwtUtil jwtUtil;

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        // Força a chave secreta para os testes
        try {
            java.lang.reflect.Field secretKeyField = JwtUtil.class.getDeclaredField("SECRET_KEY");
            secretKeyField.setAccessible(true);
            secretKeyField.set(jwtUtil, "chave-secreta-teste");
            java.lang.reflect.Field expirationField = JwtUtil.class.getDeclaredField("EXPIRATION");
            expirationField.setAccessible(true);
            expirationField.set(jwtUtil, 3600000L);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void testGenerateAndValidateToken() {
        String username = "testuser";
        String role = "ROLE_USER";
        String token = jwtUtil.generateToken(username, role);
        assertNotNull(token);
        assertTrue(jwtUtil.isTokenValid(token));
        assertEquals(username, jwtUtil.extractUsername(token));
        assertEquals(role, jwtUtil.extractRole(token));
    }

    @Test
    void testIsTokenValid_ExpiredToken() throws InterruptedException {
        // Gera um token com expiração curta para simular expiração
        JwtUtil shortLivedJwtUtil = new JwtUtil() {
            @Override
            public String generateToken(String username, String role) {
                return Jwts.builder()
                        .setSubject(username)
                        .claim("role", role)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 10)) // 10ms
                        .signWith(SignatureAlgorithm.HS256, "sua-chave-secreta-muito-forte")
                        .compact();
            }
        };
        String token = shortLivedJwtUtil.generateToken("user", "ROLE_USER");
        Thread.sleep(20); // Aguarda expirar
        assertFalse(shortLivedJwtUtil.isTokenValid(token));
    }

    @Test
    void testIsTokenValid_InvalidToken() {
        String invalidToken = "invalid.token.value";
        assertFalse(jwtUtil.isTokenValid(invalidToken));
    }
}
