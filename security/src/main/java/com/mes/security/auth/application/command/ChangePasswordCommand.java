package com.mes.security.auth.application.command;

import com.mes.common.cqrs.Command;

import javax.validation.constraints.NotBlank;

/**
 * [CQRS Pattern: Command - 變更密碼命令]
 * [SOLID: SRP - 只攜帶變更密碼所需的資料]
 *
 * 變更使用者密碼的命令，需提供舊密碼以驗證身分。
 */
public class ChangePasswordCommand implements Command {

    @NotBlank(message = "使用者 ID 不可為空")
    private String userId;

    @NotBlank(message = "舊密碼不可為空")
    private String oldPassword;

    @NotBlank(message = "新密碼不可為空")
    private String newPassword;

    public ChangePasswordCommand() {
    }

    public ChangePasswordCommand(String userId, String oldPassword, String newPassword) {
        this.userId = userId;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getOldPassword() {
        return oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
}
