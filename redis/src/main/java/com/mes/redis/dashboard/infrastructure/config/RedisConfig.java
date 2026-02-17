package com.mes.redis.dashboard.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

/**
 * [Infrastructure: Redis 設定]
 * [SOLID: SRP - 只負責 Redis 相關的基礎設施配置]
 *
 * Redis 設定類別，配置：
 * <ul>
 *   <li>RedisTemplate - 自訂序列化器（Key 用 String，Value 用 JSON）</li>
 *   <li>RedisCacheManager - Spring Cache 與 Redis 的整合</li>
 *   <li>StringRedisTemplate - 簡單字串操作的便利模板</li>
 * </ul>
 *
 * 教學重點：序列化器配置
 * <ul>
 *   <li>StringRedisSerializer - Key 使用字串格式，人類可讀</li>
 *   <li>GenericJackson2JsonRedisSerializer - Value 使用 JSON 格式，包含型別資訊</li>
 *   <li>避免使用 JDK 序列化（預設），因為不可讀且跨語言不相容</li>
 * </ul>
 */
@Configuration
@EnableCaching
public class RedisConfig {

    private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

    /**
     * 配置 Jackson ObjectMapper，支援 Java 8 日期時間序列化。
     *
     * @return 已配置的 ObjectMapper
     */
    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        log.info("Configured ObjectMapper with JavaTimeModule for Redis module");
        return mapper;
    }

    /**
     * 配置 RedisTemplate，自訂 Key/Value 序列化器。
     *
     * 教學重點：
     * - Key Serializer: StringRedisSerializer，讓 Redis 中的 Key 為人類可讀字串
     * - Value Serializer: GenericJackson2JsonRedisSerializer，將 Value 序列化為 JSON
     * - Hash Key/Value 也使用相同策略
     *
     * @param connectionFactory Redis 連線工廠
     * @return 已配置的 RedisTemplate
     */
    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Key 序列化：使用 String
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);

        // Value 序列化：使用 JSON（包含型別資訊，支援自動反序列化）
        GenericJackson2JsonRedisSerializer jsonSerializer = new GenericJackson2JsonRedisSerializer();
        template.setValueSerializer(jsonSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        log.info("Configured RedisTemplate with StringRedisSerializer (key) + GenericJackson2JsonRedisSerializer (value)");
        return template;
    }

    /**
     * 配置 RedisCacheManager，設定 Spring Cache 的 Redis 快取行為。
     *
     * 教學重點：
     * - TTL (Time-To-Live): 快取存活時間 30 分鐘
     * - Key Prefix: 所有快取鍵加上 "mes:" 前綴，避免命名衝突
     * - Null Values: 不快取 null 值，避免佔用記憶體
     *
     * @param connectionFactory Redis 連線工廠
     * @return 已配置的 RedisCacheManager
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(30))
                .prefixCacheNameWith("mes:")
                .serializeKeysWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair.fromSerializer(
                                new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        log.info("Configured RedisCacheManager with TTL=30min, prefix='mes:', no null values");
        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(cacheConfig)
                .build();
    }

    /**
     * StringRedisTemplate 便利 Bean。
     * 適用於簡單的字串鍵值操作。
     *
     * @param connectionFactory Redis 連線工廠
     * @return StringRedisTemplate
     */
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }
}
