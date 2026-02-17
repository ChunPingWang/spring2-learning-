package com.mes.security.auth.domain.model;

import com.mes.common.ddd.annotation.AggregateRoot;
import com.mes.common.ddd.model.BaseAggregateRoot;
import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.DomainException;
import com.mes.security.auth.domain.event.RoleAssignedEvent;
import com.mes.security.auth.domain.event.UserCreatedEvent;
import com.mes.security.auth.domain.event.UserLockedEvent;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * [DDD Pattern: Aggregate Root - 使用者聚合根]
 * [SOLID: SRP - 只負責使用者的認證與授權相關行為]
 * [SOLID: OCP - 可透過新增角色和權限擴展使用者能力]
 *
 * User 是安全模組的核心聚合根，負責：
 * 1. 管理使用者的角色 (RBAC)
 * 2. 維護使用者的鎖定/啟用狀態
 * 3. 管理密碼變更
 * 4. 檢查使用者的權限
 *
 * 所有狀態變更都會在適當時機註冊 Domain Event。
 */
@AggregateRoot
public class User extends BaseAggregateRoot<UserId> {

    private Username username;
    private String encodedPassword;
    private Email email;
    private Set<Role> roles;
    private boolean enabled;
    private boolean locked;

    /**
     * 建立新使用者。
     *
     * @param id              使用者 ID
     * @param username        使用者名稱
     * @param encodedPassword 已編碼的密碼
     * @param email           電子郵件
     */
    public User(UserId id, Username username, String encodedPassword, Email email) {
        super(id);
        if (username == null) {
            throw new DomainException("Username must not be null");
        }
        if (encodedPassword == null || encodedPassword.trim().isEmpty()) {
            throw new DomainException("Encoded password must not be blank");
        }
        if (email == null) {
            throw new DomainException("Email must not be null");
        }
        this.username = username;
        this.encodedPassword = encodedPassword;
        this.email = email;
        this.roles = new HashSet<Role>();
        this.enabled = true;
        this.locked = false;
        registerEvent(new UserCreatedEvent(id.getValue(), username.getValue(), email.getValue()));
    }

    /**
     * 指派角色給使用者。
     *
     * @param role 要指派的角色
     * @throws BusinessRuleViolationException 若角色已存在
     */
    public void assignRole(Role role) {
        if (role == null) {
            throw new DomainException("Role must not be null");
        }
        if (roles.contains(role)) {
            throw new BusinessRuleViolationException(
                    "User already has role: " + role.getName());
        }
        roles.add(role);
        touch();
        registerEvent(new RoleAssignedEvent(getId().getValue(), role.getName()));
    }

    /**
     * 移除使用者的角色。
     *
     * @param roleName 要移除的角色名稱
     * @throws BusinessRuleViolationException 若角色不存在
     */
    public void removeRole(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new DomainException("Role name must not be blank");
        }
        Role toRemove = null;
        for (Role role : roles) {
            if (role.getName().equalsIgnoreCase(roleName)) {
                toRemove = role;
                break;
            }
        }
        if (toRemove == null) {
            throw new BusinessRuleViolationException(
                    "User does not have role: " + roleName);
        }
        roles.remove(toRemove);
        touch();
    }

    /**
     * 變更密碼。
     *
     * @param newEncodedPassword 新的已編碼密碼
     */
    public void changePassword(String newEncodedPassword) {
        if (newEncodedPassword == null || newEncodedPassword.trim().isEmpty()) {
            throw new DomainException("New encoded password must not be blank");
        }
        this.encodedPassword = newEncodedPassword;
        touch();
    }

    /**
     * 鎖定使用者帳號。
     *
     * @param reason 鎖定原因
     * @throws BusinessRuleViolationException 若帳號已被鎖定
     */
    public void lock(String reason) {
        if (locked) {
            throw new BusinessRuleViolationException("User is already locked");
        }
        this.locked = true;
        touch();
        registerEvent(new UserLockedEvent(getId().getValue(), reason));
    }

    /**
     * 解鎖使用者帳號。
     *
     * @throws BusinessRuleViolationException 若帳號未被鎖定
     */
    public void unlock() {
        if (!locked) {
            throw new BusinessRuleViolationException("User is not locked");
        }
        this.locked = false;
        touch();
    }

    /**
     * 檢查使用者是否擁有指定資源和動作的權限。
     *
     * @param resource 資源名稱
     * @param action   動作名稱
     * @return 若擁有該權限則回傳 true
     */
    public boolean hasPermission(String resource, String action) {
        for (Role role : roles) {
            if (role.hasPermission(resource, action)) {
                return true;
            }
        }
        return false;
    }

    // ========== Getters ==========

    public Username getUsername() {
        return username;
    }

    public String getEncodedPassword() {
        return encodedPassword;
    }

    public Email getEmail() {
        return email;
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public boolean isLocked() {
        return locked;
    }
}
