package com.mes.security.auth.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event - 角色指派事件]
 * [SOLID: SRP - 只負責攜帶角色指派的相關資訊]
 *
 * 當角色被指派給使用者時觸發此事件。
 * 可用於：記錄權限變更稽核日誌等。
 */
public class RoleAssignedEvent extends BaseDomainEvent {

    private final String userId;
    private final String roleName;

    public RoleAssignedEvent(String userId, String roleName) {
        super(userId);
        this.userId = userId;
        this.roleName = roleName;
    }

    public String getUserId() {
        return userId;
    }

    public String getRoleName() {
        return roleName;
    }

    @Override
    public String toString() {
        return "RoleAssignedEvent{" +
                "userId='" + userId + '\'' +
                ", roleName='" + roleName + '\'' +
                '}';
    }
}
