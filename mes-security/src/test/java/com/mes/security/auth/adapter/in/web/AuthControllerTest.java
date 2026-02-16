package com.mes.security.auth.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mes.common.cqrs.CommandBus;
import com.mes.common.cqrs.QueryBus;
import com.mes.security.auth.application.command.CreateUserCommand;
import com.mes.security.auth.application.port.PasswordEncoderPort;
import com.mes.security.auth.infrastructure.security.JwtTokenProvider;
import com.mes.security.auth.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * [Hexagonal Architecture: Inbound Adapter 測試]
 *
 * 使用 @WebMvcTest 測試 AuthController 的 HTTP 端點。
 * Mock 所有依賴的 Service 和 Bus。
 */
@DisplayName("AuthController 測試")
@WebMvcTest(AuthController.class)
@Import(SecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommandBus commandBus;

    @MockBean
    private QueryBus queryBus;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private BCryptPasswordEncoder passwordEncoder;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private PasswordEncoderPort passwordEncoderPort;

    @MockBean
    private JwtDecoder jwtDecoder;

    @Nested
    @DisplayName("POST /api/v1/auth/login - 登入")
    class LoginTests {

        @Test
        @DisplayName("合法的帳號密碼應回傳 200 及 JWT Token")
        void shouldReturn200WithTokenForValidCredentials() throws Exception {
            // Arrange
            User userDetails = new User("admin", "$2a$10$encoded",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
            when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
            when(passwordEncoder.matches("password", "$2a$10$encoded")).thenReturn(true);
            when(jwtTokenProvider.generateToken(anyString(), any(java.util.Set.class)))
                    .thenReturn("mock.jwt.token");

            LoginRequest request = new LoginRequest("admin", "password");

            // Act & Assert
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").value("mock.jwt.token"));
        }

        @Test
        @DisplayName("錯誤的密碼應回傳 401 Unauthorized")
        void shouldReturn401ForInvalidPassword() throws Exception {
            // Arrange
            User userDetails = new User("admin", "$2a$10$encoded",
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));
            when(userDetailsService.loadUserByUsername("admin")).thenReturn(userDetails);
            when(passwordEncoder.matches("wrongpassword", "$2a$10$encoded")).thenReturn(false);

            LoginRequest request = new LoginRequest("admin", "wrongpassword");

            // Act & Assert
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isUnauthorized())
                    .andExpect(jsonPath("$.code").value(401));
        }

        @Test
        @DisplayName("缺少使用者名稱應回傳 400 Bad Request")
        void shouldReturn400WhenUsernameMissing() throws Exception {
            String json = "{\"password\":\"pass\"}";

            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("POST /api/v1/auth/register - 註冊")
    class RegisterTests {

        @Test
        @DisplayName("合法請求應回傳 201 Created")
        void shouldReturn201ForValidRegistration() throws Exception {
            // Arrange
            when(commandBus.<CreateUserCommand, String>dispatch(any()))
                    .thenReturn("user-id-123");

            CreateUserCommand command = new CreateUserCommand(
                    "new_user", "password123", "new@example.com",
                    Arrays.asList("VIEWER"));

            // Act & Assert
            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(201))
                    .andExpect(jsonPath("$.data").value("user-id-123"));
        }

        @Test
        @DisplayName("缺少必要欄位應回傳 400 Bad Request")
        void shouldReturn400ForMissingFields() throws Exception {
            String json = "{}";

            mockMvc.perform(post("/api/v1/auth/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(json))
                    .andExpect(status().isBadRequest());
        }
    }
}
