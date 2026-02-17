package com.mes.security.auth.application.query.dto;

import java.util.List;

/**
 * [CQRS Pattern: Query DTO - 角色檢視]
 * [SOLID: SRP - 只負責呈現角色的唯讀資料]
 *
 * 用於 Query Side 的角色資料傳輸物件。
 */
public class RoleView {

    private String name;
    private String description;
    private List<String> permissionDescriptions;

    public RoleView() {
    }

    public RoleView(String name, String description, List<String> permissionDescriptions) {
        this.name = name;
        this.description = description;
        this.permissionDescriptions = permissionDescriptions;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getPermissionDescriptions() {
        return permissionDescriptions;
    }

    public void setPermissionDescriptions(List<String> permissionDescriptions) {
        this.permissionDescriptions = permissionDescriptions;
    }
}
