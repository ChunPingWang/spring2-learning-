package com.mes.security.auth.application.query.dto;

import java.time.LocalDateTime;
import java.util.List;

/**
 * [CQRS Pattern: Query DTO - 使用者檢視]
 * [SOLID: SRP - 只負責呈現使用者的唯讀資料]
 *
 * 用於 Query Side 的使用者資料傳輸物件。
 * 不包含敏感資訊（如密碼）。
 */
public class UserView {

    private String id;
    private String username;
    private String email;
    private List<String> roleNames;
    private boolean enabled;
    private boolean locked;
    private LocalDateTime createdAt;

    public UserView() {
    }

    public UserView(String id, String username, String email, List<String> roleNames,
                    boolean enabled, boolean locked, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.roleNames = roleNames;
        this.enabled = enabled;
        this.locked = locked;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getRoleNames() {
        return roleNames;
    }

    public void setRoleNames(List<String> roleNames) {
        this.roleNames = roleNames;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
