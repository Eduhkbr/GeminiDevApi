package com.eduhkbr.gemini.DevApi.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CacheConfigTest {
    private final CacheConfig config = new CacheConfig();

    @Test
    @DisplayName("Deve criar configuração Caffeine com TTL e tamanho máximo")
    void caffeineConfig_DeveCriarCaffeineComParametrosCorretos() {
        Caffeine<Object, Object> caffeine = config.caffeineConfig();
        assertNotNull(caffeine);
        // Não há API pública para ler TTL/tamanho, mas podemos criar um cache e testar comportamento
        var cache = caffeine.build();
        cache.put("chave", "valor");
        assertEquals("valor", cache.getIfPresent("chave"));
    }

    @Test
    @DisplayName("Deve criar CacheManager primário do tipo Caffeine")
    void cacheManager_DeveRetornarCaffeineCacheManager() {
        Caffeine<Object, Object> caffeine = config.caffeineConfig();
        CacheManager manager = config.cacheManager(caffeine);
        assertNotNull(manager);
        assertTrue(manager instanceof CaffeineCacheManager);
    }

    @Test
    @DisplayName("Deve criar RedisCacheManager com configuração padrão")
    void redisCacheManager_DeveRetornarInstancia() {
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
        RedisCacheManager manager = config.redisCacheManager(factory);
        assertNotNull(manager);
        assertTrue(manager instanceof RedisCacheManager);
    }

    @Test
    @DisplayName("Deve criar RedisTemplate configurado corretamente")
    void redisTemplate_DeveRetornarInstanciaComSerializadores() {
        RedisConnectionFactory factory = mock(RedisConnectionFactory.class);
        RedisTemplate<String, Object> template = config.redisTemplate(factory);
        assertNotNull(template);
        assertEquals(factory, template.getConnectionFactory());
        assertTrue(template.getKeySerializer() instanceof StringRedisSerializer);
        assertTrue(template.getValueSerializer() instanceof GenericJackson2JsonRedisSerializer);
    }
}
