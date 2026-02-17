package com.mes.security.auth.application.command;

import com.mes.common.cqrs.Command;

import javax.validation.constraints.NotBlank;

/**
 * [CQRS Pattern: Command - 鎖定使用者命令]
 * [SOLID: SRP - 只攜帶鎖定使用者所需的資料]
 *
 * 鎖定使用者帳號的命令，需提供鎖定原因。
 */
public class LockUserCommand implements Command {

    @NotBlank(message = "使用者 ID 不可為空")
    private String userId;

    @NotBlank(message = "鎖定原因不可為空")
    private String reason;

    public LockUserCommand() {
    }

    public LockUserCommand(String userId, String reason) {
        this.userId = userId;
        this.reason = reason;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
