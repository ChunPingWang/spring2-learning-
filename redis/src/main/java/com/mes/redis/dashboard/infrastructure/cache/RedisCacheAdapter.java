package com.mes.redis.dashboard.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mes.redis.dashboard.domain.port.out.CachePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * [DDD Pattern: Adapter - Redis 快取配接器]
 * [SOLID: LSP - 完整實作 CachePort 介面的契約]
 * [SOLID: DIP - 實作領域層定義的 CachePort 介面]
 * [Hexagonal Architecture: Output Adapter - 使用 Redis 作為快取出站機制]
 *
 * 使用 RedisTemplate 實作 CachePort 介面。
 * 領域層不知道 Redis 的存在，只透過 CachePort 操作快取。
 *
 * 教學重點：Redis 五種資料型別操作
 * <pre>
 * 1. opsForValue()  - String 類型操作（本快取使用此方式，搭配 JSON 序列化）
 *    - set(key, value)
 *    - get(key)
 *    - set(key, value, timeout, timeUnit)
 *
 * 2. opsForHash()   - Hash 類型操作（適合存儲物件的多個欄位）
 *    - put(key, hashKey, value)
 *    - get(key, hashKey)
 *    - entries(key)
 *
 * 3. opsForList()   - List 類型操作（適合佇列、歷史記錄）
 *    - rightPush(key, value)
 *    - leftPop(key)
 *    - range(key, start, end)
 *
 * 4. opsForSet()    - Set 類型操作（適合標籤、唯一值集合）
 *    - add(key, values...)
 *    - members(key)
 *    - isMember(key, value)
 *
 * 5. opsForZSet()   - Sorted Set 類型操作（適合排行榜、優先級佇列）
 *    - add(key, value, score)
 *    - range(key, start, end)
 *    - rangeByScore(key, min, max)
 * </pre>
 */
@Component
public class RedisCacheAdapter implements CachePort {

    private static final Logger log = LoggerFactory.getLogger(RedisCacheAdapter.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    public RedisCacheAdapter(RedisTemplate<String, Object> redisTemplate, ObjectMapper objectMapper) {
        this.redisTemplate = redisTemplate;
        this.objectMapper = objectMapper;
    }

    @Override
    public <T> void put(String key, T value, long ttlSeconds) {
        try {
            // 使用 opsForValue (String) 搭配 JSON 序列化
            redisTemplate.opsForValue().set(key, value, ttlSeconds, TimeUnit.SECONDS);
            log.debug("Cache PUT: key={}, ttl={}s", key, ttlSeconds);
        } catch (Exception e) {
            log.warn("Failed to put cache: key={}, error={}", key, e.getMessage());
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                log.debug("Cache GET: key={}, result=MISS", key);
                return null;
            }
            log.debug("Cache GET: key={}, result=HIT", key);
            // 若 value 已是目標型別（GenericJackson2JsonRedisSerializer 會自動反序列化），直接回傳
            if (type.isInstance(value)) {
                return (T) value;
            }
            // 否則透過 ObjectMapper 手動轉換
            return objectMapper.convertValue(value, type);
        } catch (Exception e) {
            log.warn("Failed to get cache: key={}, error={}", key, e.getMessage());
            return null;
        }
    }

    @Override
    public void evict(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("Cache EVICT: key={}", key);
        } catch (Exception e) {
            log.warn("Failed to evict cache: key={}, error={}", key, e.getMessage());
        }
    }

    @Override
    public void evictByPattern(String pattern) {
        try {
            Set<String> keys = redisTemplate.keys(pattern);
            if (keys != null && !keys.isEmpty()) {
                redisTemplate.delete(keys);
                log.debug("Cache EVICT BY PATTERN: pattern={}, keysEvicted={}", pattern, keys.size());
            }
        } catch (Exception e) {
            log.warn("Failed to evict cache by pattern: pattern={}, error={}", pattern, e.getMessage());
        }
    }

    @Override
    public boolean exists(String key) {
        try {
            Boolean exists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(exists);
        } catch (Exception e) {
            log.warn("Failed to check cache existence: key={}, error={}", key, e.getMessage());
            return false;
        }
    }
}
