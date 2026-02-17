package com.mes.security.auth.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event - 使用者鎖定事件]
 * [SOLID: SRP - 只負責攜帶使用者鎖定的相關資訊]
 *
 * 當使用者帳號被鎖定時觸發此事件。
 * 可用於：安全告警、稽核紀錄等。
 */
public class UserLockedEvent extends BaseDomainEvent {

    private final String userId;
    private final String reason;

    public UserLockedEvent(String userId, String reason) {
        super(userId);
        this.userId = userId;
        this.reason = reason;
    }

    public String getUserId() {
        return userId;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public String toString() {
        return "UserLockedEvent{" +
                "userId='" + userId + '\'' +
                ", reason='" + reason + '\'' +
                '}';
    }
}
