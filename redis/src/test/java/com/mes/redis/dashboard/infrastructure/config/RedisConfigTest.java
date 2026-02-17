package com.mes.redis.dashboard.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * RedisConfig 設定類別單元測試。
 * 直接測試設定類別方法，驗證 Bean 建立是否正確。
 */
@DisplayName("RedisConfig 設定類別測試")
class RedisConfigTest {

    private RedisConfig redisConfig;
    private RedisConnectionFactory connectionFactory;

    @BeforeEach
    void setUp() {
        redisConfig = new RedisConfig();
        connectionFactory = mock(RedisConnectionFactory.class);
    }

    @Test
    @DisplayName("objectMapper 應配置 JavaTimeModule")
    void shouldCreateObjectMapperWithJavaTimeModule() {
        ObjectMapper mapper = redisConfig.objectMapper();

        assertThat(mapper).isNotNull();
        // 驗證可以處理 LocalDateTime（已註冊 JavaTimeModule）
        assertThat(mapper.getRegisteredModuleIds()).isNotEmpty();
    }

    @Test
    @DisplayName("redisTemplate 應使用 StringRedisSerializer 作為 Key 序列化器")
    void shouldCreateRedisTemplateWithStringKeySerializer() {
        RedisTemplate<String, Object> template = redisConfig.redisTemplate(connectionFactory);

        assertThat(template).isNotNull();
        assertThat(template.getKeySerializer()).isInstanceOf(StringRedisSerializer.class);
        assertThat(template.getHashKeySerializer()).isInstanceOf(StringRedisSerializer.class);
    }

    @Test
    @DisplayName("redisTemplate 應使用 GenericJackson2JsonRedisSerializer 作為 Value 序列化器")
    void shouldCreateRedisTemplateWithJsonValueSerializer() {
        RedisTemplate<String, Object> template = redisConfig.redisTemplate(connectionFactory);

        assertThat(template).isNotNull();
        assertThat(template.getValueSerializer()).isInstanceOf(GenericJackson2JsonRedisSerializer.class);
        assertThat(template.getHashValueSerializer()).isInstanceOf(GenericJackson2JsonRedisSerializer.class);
    }

    @Test
    @DisplayName("redisCacheManager 應成功建立")
    void shouldCreateRedisCacheManager() {
        RedisCacheManager cacheManager = redisConfig.redisCacheManager(connectionFactory);

        assertThat(cacheManager).isNotNull();
    }

    @Test
    @DisplayName("stringRedisTemplate 應成功建立")
    void shouldCreateStringRedisTemplate() {
        StringRedisTemplate stringTemplate = redisConfig.stringRedisTemplate(connectionFactory);

        assertThat(stringTemplate).isNotNull();
    }
}
