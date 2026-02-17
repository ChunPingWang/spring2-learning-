package com.mes.security.auth.application.command;

import com.mes.common.cqrs.Command;

import javax.validation.constraints.NotBlank;

/**
 * [CQRS Pattern: Command - 指派角色命令]
 * [SOLID: SRP - 只攜帶指派角色所需的資料]
 *
 * 將角色指派給使用者的命令。
 */
public class AssignRoleCommand implements Command {

    @NotBlank(message = "使用者 ID 不可為空")
    private String userId;

    @NotBlank(message = "角色名稱不可為空")
    private String roleName;

    public AssignRoleCommand() {
    }

    public AssignRoleCommand(String userId, String roleName) {
        this.userId = userId;
        this.roleName = roleName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }
}
