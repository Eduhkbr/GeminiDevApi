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
