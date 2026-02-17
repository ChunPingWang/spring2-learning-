package com.mes.security.auth.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event - 使用者建立事件]
 * [SOLID: SRP - 只負責攜帶使用者建立的相關資訊]
 *
 * 當新使用者被建立時觸發此事件。
 * 可用於：發送歡迎郵件、建立稽核紀錄等。
 */
public class UserCreatedEvent extends BaseDomainEvent {

    private final String username;
    private final String email;

    public UserCreatedEvent(String aggregateId, String username, String email) {
        super(aggregateId);
        this.username = username;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "UserCreatedEvent{" +
                "username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", aggregateId='" + getAggregateId() + '\'' +
                '}';
    }
}
