package com.mes.redis.dashboard.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 * [Infrastructure: 自訂快取 Key 生成器]
 * [SOLID: SRP - 只負責生成快取鍵]
 *
 * 自訂的 Spring Cache Key Generator。
 * 當 {@code @Cacheable} 等註解未指定 key 時，使用此生成器。
 *
 * 教學重點：
 * Spring Cache 預設使用方法參數作為快取鍵。
 * 自訂 KeyGenerator 可以產生更有意義的鍵名稱，
 * 例如包含類別名和方法名，避免不同 Service 的相同參數產生鍵衝突。
 *
 * 使用方式：
 * <pre>
 * {@code @Cacheable(value = "myCache", keyGenerator = "redisKeyGenerator")}
 * </pre>
 */
@Component("redisKeyGenerator")
public class RedisKeyGenerator implements KeyGenerator {

    private static final Logger log = LoggerFactory.getLogger(RedisKeyGenerator.class);

    @Override
    public Object generate(Object target, Method method, Object... params) {
        StringBuilder keyBuilder = new StringBuilder();
        keyBuilder.append(target.getClass().getSimpleName())
                .append(":")
                .append(method.getName());
        if (params.length > 0) {
            keyBuilder.append(":")
                    .append(Arrays.deepToString(params));
        }
        String key = keyBuilder.toString();
        log.debug("Generated cache key: {}", key);
        return key;
    }
}
