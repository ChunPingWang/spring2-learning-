package com.mes.redis.dashboard.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * [DDD Pattern: Value Object - 指標時間範圍]
 * [SOLID: SRP - 只負責封裝與驗證時間範圍]
 *
 * 不可變的時間範圍值物件。
 * 驗證 from 必須早於 to。
 */
@ValueObject
public class MetricsTimeRange extends BaseValueObject {

    private final LocalDateTime from;
    private final LocalDateTime to;

    public MetricsTimeRange(LocalDateTime from, LocalDateTime to) {
        this.from = Objects.requireNonNull(from, "From time must not be null");
        this.to = Objects.requireNonNull(to, "To time must not be null");
        if (!from.isBefore(to)) {
            throw new IllegalArgumentException("From time must be before to time");
        }
    }

    public LocalDateTime getFrom() {
        return from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.asList(from, to);
    }
}
