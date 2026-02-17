package com.mes.security.auth.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;
import com.mes.common.exception.DomainException;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * [DDD Pattern: Value Object - 角色]
 * [SOLID: SRP - 只負責角色的描述與權限檢查]
 * [SOLID: OCP - 可透過新增 Permission 擴展角色能力，無需修改此類別]
 *
 * Role 代表系統中的角色，包含角色名稱、描述及其擁有的權限集合。
 * 不可變物件，相等性由 name 決定。
 *
 * 預定義常數：ROLE_ADMIN、ROLE_OPERATOR、ROLE_VIEWER。
 */
@ValueObject
public final class Role extends BaseValueObject {

    /** 管理員角色 - 擁有所有權限 */
    public static final Role ROLE_ADMIN = createAdmin();

    /** 操作員角色 - 擁有讀取和寫入權限 */
    public static final Role ROLE_OPERATOR = createOperator();

    /** 檢視者角色 - 僅擁有讀取權限 */
    public static final Role ROLE_VIEWER = createViewer();

    private final String name;
    private final String description;
    private final Set<Permission> permissions;

    /**
     * 建構 Role。
     *
     * @param name        角色名稱，如 "ADMIN"、"OPERATOR"
     * @param description 角色描述
     * @param permissions 角色擁有的權限集合
     */
    public Role(String name, String description, Set<Permission> permissions) {
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("Role name must not be blank");
        }
        this.name = name.toUpperCase();
        this.description = description != null ? description : "";
        this.permissions = permissions != null
                ? Collections.unmodifiableSet(new HashSet<Permission>(permissions))
                : Collections.<Permission>emptySet();
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    /**
     * 檢查此角色是否擁有指定資源和動作的權限。
     *
     * @param resource 資源名稱
     * @param action   動作名稱
     * @return 若擁有該權限則回傳 true
     */
    public boolean hasPermission(String resource, String action) {
        for (Permission permission : permissions) {
            if (permission.matches(resource, action)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.<Object>asList(name);
    }

    @Override
    public String toString() {
        return "Role{name='" + name + "', permissions=" + permissions + "}";
    }

    // ========== 預定義角色工廠方法 ==========

    private static Role createAdmin() {
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(new Permission("WORK_ORDER", "READ"));
        permissions.add(new Permission("WORK_ORDER", "WRITE"));
        permissions.add(new Permission("WORK_ORDER", "DELETE"));
        permissions.add(new Permission("EQUIPMENT", "READ"));
        permissions.add(new Permission("EQUIPMENT", "WRITE"));
        permissions.add(new Permission("EQUIPMENT", "DELETE"));
        permissions.add(new Permission("USER", "READ"));
        permissions.add(new Permission("USER", "WRITE"));
        permissions.add(new Permission("USER", "DELETE"));
        return new Role("ADMIN", "系統管理員 - 擁有所有權限", permissions);
    }

    private static Role createOperator() {
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(new Permission("WORK_ORDER", "READ"));
        permissions.add(new Permission("WORK_ORDER", "WRITE"));
        permissions.add(new Permission("EQUIPMENT", "READ"));
        permissions.add(new Permission("EQUIPMENT", "WRITE"));
        permissions.add(new Permission("USER", "READ"));
        return new Role("OPERATOR", "操作員 - 擁有讀取和寫入權限", permissions);
    }

    private static Role createViewer() {
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(new Permission("WORK_ORDER", "READ"));
        permissions.add(new Permission("EQUIPMENT", "READ"));
        permissions.add(new Permission("USER", "READ"));
        return new Role("VIEWER", "檢視者 - 僅擁有讀取權限", permissions);
    }
}
