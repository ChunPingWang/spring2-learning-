package com.mes.redis.dashboard.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event - 快取已失效事件]
 * [SOLID: SRP - 只負責攜帶快取失效的事實資訊]
 *
 * 當快取鍵被手動清除或因過期而失效時觸發。
 * 不可變：事件一旦建立就不能修改。
 */
public class CacheInvalidatedEvent extends BaseDomainEvent {

    private final String cacheKey;
    private final String reason;

    public CacheInvalidatedEvent(String aggregateId, String cacheKey, String reason) {
        super(aggregateId);
        this.cacheKey = cacheKey;
        this.reason = reason;
    }

    public String getCacheKey() {
        return cacheKey;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "CacheInvalidatedEvent{" +
                "eventId='" + getEventId() + '\'' +
                ", cacheKey='" + cacheKey + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
