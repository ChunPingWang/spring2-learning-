package com.mes.security.auth.application.command;

import com.mes.common.cqrs.Command;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * [CQRS Pattern: Command - 建立使用者命令]
 * [SOLID: SRP - 只攜帶建立使用者所需的資料]
 *
 * 建立新使用者的命令，包含使用者名稱、密碼、電子郵件及初始角色。
 */
public class CreateUserCommand implements Command {

    @NotBlank(message = "使用者名稱不可為空")
    private String username;

    @NotBlank(message = "密碼不可為空")
    private String password;

    @NotBlank(message = "電子郵件不可為空")
    private String email;

    private List<String> roleNames;

    public CreateUserCommand() {
    }

    public CreateUserCommand(String username, String password, String email, List<String> roleNames) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.roleNames = roleNames;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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
}
