package com.mes.testing.session;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Session 控制器測試")
class SessionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("POST /api/v1/session/login - 應該成功登入並建立 Session")
    void login_shouldCreateSession() throws Exception {
        mockMvc.perform(post("/api/v1/session/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"role\":\"ADMIN\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("admin"))
                .andExpect(jsonPath("$.loggedIn").doesNotExist());
    }

    @Test
    @DisplayName("GET /api/v1/session/user - 未登入應該顯示未登入")
    void getUser_notLoggedIn_shouldReturnFalse() throws Exception {
        mockMvc.perform(get("/api/v1/session/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.loggedIn").value(false));
    }

    @Test
    @DisplayName("GET /api/v1/session/user - 登入後應該顯示使用者資訊")
    void getUser_afterLogin_shouldReturnUserInfo() throws Exception {
        mockMvc.perform(post("/api/v1/session/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"testuser\",\"role\":\"USER\"}"));

        mockMvc.perform(get("/api/v1/session/user"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.role").value("USER"))
                .andExpect(jsonPath("$.loggedIn").value(true));
    }

    @Test
    @DisplayName("POST /api/v1/session/logout - 應該成功登出")
    void logout_shouldInvalidateSession() throws Exception {
        mockMvc.perform(post("/api/v1/session/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\":\"admin\",\"role\":\"ADMIN\"}"));

        mockMvc.perform(post("/api/v1/session/logout"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("已登出"));
    }

    @Test
    @DisplayName("GET /api/v1/session/attributes - 應該回傳 Session 屬性")
    void getAttributes_shouldReturnSessionInfo() throws Exception {
        mockMvc.perform(get("/api/v1/session/attributes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.creationTime").exists())
                .andExpect(jsonPath("$.maxInactiveInterval").exists());
    }

    @Test
    @DisplayName("POST /api/v1/session/set-attribute - 應該設定自訂屬性")
    void setAttribute_shouldStoreCustomAttribute() throws Exception {
        mockMvc.perform(post("/api/v1/session/set-attribute")
                        .param("key", "customKey")
                        .param("value", "customValue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.key").value("customKey"))
                .andExpect(jsonPath("$.value").value("customValue"));
    }
}
