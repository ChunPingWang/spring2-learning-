package com.mes.security.auth.adapter.in.web;

import javax.validation.constraints.NotBlank;

/**
 * [Hexagonal Architecture: Adapter - 登入請求 DTO]
 * [SOLID: SRP - 只負責攜帶登入請求的資料]
 *
 * 登入端點的請求資料，包含使用者名稱和密碼。
 * 使用 Bean Validation 進行輸入驗證。
 */
public class LoginRequest {

    @NotBlank(message = "使用者名稱不可為空")
    private String username;

    @NotBlank(message = "密碼不可為空")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
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
}
