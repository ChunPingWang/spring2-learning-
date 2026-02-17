package com.mes.redis.dashboard.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * [DDD Pattern: Value Object - 快取過期設定]
 * [SOLID: SRP - 只負責判斷快取是否過期]
 *
 * 不可變的快取過期值物件。
 * 包含 TTL（存活時間秒數）與建立時間，
 * 提供 {@link #isExpired()} 方法判斷快取是否已過期。
 */
@ValueObject
public class CacheExpiry extends BaseValueObject {

    private final int ttlSeconds;
    private final LocalDateTime createdAt;

    public CacheExpiry(int ttlSeconds, LocalDateTime createdAt) {
        if (ttlSeconds <= 0) {
            throw new IllegalArgumentException("TTL seconds must be positive");
        }
        this.ttlSeconds = ttlSeconds;
        this.createdAt = Objects.requireNonNull(createdAt, "Created time must not be null");
    }

    /**
     * 判斷快取是否已過期。
     * 當前時間超過 createdAt + ttlSeconds 即視為過期。
     *
     * @return true 表示已過期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(createdAt.plusSeconds(ttlSeconds));
    }

    public int getTtlSeconds() {
        return ttlSeconds;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.asList(ttlSeconds, createdAt);
    }
}
