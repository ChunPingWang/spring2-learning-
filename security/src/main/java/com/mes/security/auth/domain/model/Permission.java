package com.mes.security.auth.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;
import com.mes.common.exception.DomainException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * [DDD Pattern: Value Object - 權限]
 * [SOLID: SRP - 只負責描述對某資源的某動作權限]
 *
 * Permission 表示對特定資源的特定操作權限。
 * 例如：new Permission("WORK_ORDER", "READ") 表示對工單的讀取權限。
 *
 * 不可變物件，相等性由 resource 和 action 共同決定。
 */
@ValueObject
public final class Permission extends BaseValueObject {

    private final String resource;
    private final String action;

    /**
     * 建構 Permission。
     *
     * @param resource 資源名稱，如 "WORK_ORDER"、"EQUIPMENT"
     * @param action   操作名稱，如 "READ"、"WRITE"、"DELETE"
     */
    public Permission(String resource, String action) {
        if (resource == null || resource.trim().isEmpty()) {
            throw new DomainException("Permission resource must not be blank");
        }
        if (action == null || action.trim().isEmpty()) {
            throw new DomainException("Permission action must not be blank");
        }
        this.resource = resource.toUpperCase();
        this.action = action.toUpperCase();
    }

    public String getResource() {
        return resource;
    }

    public String getAction() {
        return action;
    }

    /**
     * 檢查此權限是否匹配指定的資源和動作。
     *
     * @param resource 資源名稱
     * @param action   動作名稱
     * @return 若匹配則回傳 true
     */
    public boolean matches(String resource, String action) {
        return this.resource.equalsIgnoreCase(resource)
                && this.action.equalsIgnoreCase(action);
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.<Object>asList(resource, action);
    }

    @Override
    public String toString() {
        return resource + ":" + action;
    }
}
