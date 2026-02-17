package com.mes.security.auth.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mes.security.auth.adapter.in.web.LoginRequest;
import com.mes.security.auth.application.command.CreateUserCommand;
import com.mes.security.auth.application.port.PasswordEncoderPort;
import com.mes.security.auth.domain.model.Email;
import com.mes.security.auth.domain.model.Role;
import com.mes.security.auth.domain.model.User;
import com.mes.security.auth.domain.model.UserId;
import com.mes.security.auth.domain.model.Username;
import com.mes.security.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * [Hexagonal Architecture: 整合測試]
 *
 * 啟動完整的 Spring Boot 應用上下文，測試安全流程的端到端鏈路：
 * 1. 未認證請求 -> 401
 * 2. 註冊 -> 登入 -> 取得 JWT -> 存取受保護端點 -> 200
 * 3. 錯誤角色 -> 403
 */
@DisplayName("安全模組整合測試")
@SpringBootTest
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoderPort passwordEncoderPort;

    @BeforeEach
    void setUp() {
        // 建立一個 Admin 使用者用於測試
        String encodedPassword = passwordEncoderPort.encode("admin123");
        User admin = new User(
                UserId.of("admin-id"),
                new Username("admin"),
                encodedPassword,
                new Email("admin@example.com")
        );
        admin.assignRole(Role.ROLE_ADMIN);
        userRepository.save(admin);

        // 建立一個 Viewer 使用者用於測試
        String viewerEncodedPassword = passwordEncoderPort.encode("viewer123");
        User viewer = new User(
                UserId.of("viewer-id"),
                new Username("viewer"),
                viewerEncodedPassword,
                new Email("viewer@example.com")
        );
        viewer.assignRole(Role.ROLE_VIEWER);
        userRepository.save(viewer);
    }

    @Test
    @DisplayName("未認證的請求應回傳 401 Unauthorized")
    void shouldReturn401ForUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/v1/users/some-id"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("登入成功後應回傳 JWT Token")
    void shouldReturnJwtTokenOnSuccessfulLogin() throws Exception {
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");

        MvcResult result = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isNotEmpty())
                .andReturn();

        String responseBody = result.getResponse().getContentAsString();
        assertThat(responseBody).contains("data");
    }

    @Test
    @DisplayName("使用有效 JWT Token 應可存取受保護端點")
    void shouldAccessProtectedEndpointWithValidToken() throws Exception {
        // Step 1: 登入取得 Token
        LoginRequest loginRequest = new LoginRequest("admin", "admin123");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        // 從回應中提取 JWT Token
        String responseBody = loginResult.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> responseMap = objectMapper.readValue(responseBody, java.util.Map.class);
        String token = (String) responseMap.get("data");
        assertThat(token).isNotNull().isNotEmpty();

        // Step 2: 使用 Token 存取受保護端點
        mockMvc.perform(get("/api/v1/users/admin-id")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value("admin"));
    }

    @Test
    @DisplayName("註冊新使用者應回傳 201")
    void shouldRegisterNewUser() throws Exception {
        CreateUserCommand command = new CreateUserCommand(
                "newuser", "newpass123", "newuser@example.com",
                Arrays.asList("OPERATOR"));

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(command)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.data").isNotEmpty());
    }

    @Test
    @DisplayName("Viewer 角色不應可存取使用者管理端點 (403)")
    void shouldReturn403ForViewerAccessingUserEndpoint() throws Exception {
        // Step 1: 登入 Viewer
        LoginRequest loginRequest = new LoginRequest("viewer", "viewer123");
        MvcResult loginResult = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String responseBody = loginResult.getResponse().getContentAsString();
        @SuppressWarnings("unchecked")
        java.util.Map<String, Object> responseMap = objectMapper.readValue(responseBody, java.util.Map.class);
        String token = (String) responseMap.get("data");

        // Step 2: Viewer 嘗試存取需要 ADMIN/OPERATOR 角色的端點
        mockMvc.perform(get("/api/v1/users/admin-id")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }
}
