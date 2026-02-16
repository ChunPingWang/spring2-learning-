package com.mes.security.auth.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mes.common.cqrs.CommandBus;
import com.mes.common.cqrs.QueryBus;
import com.mes.security.auth.application.command.AssignRoleCommand;
import com.mes.security.auth.application.command.ChangePasswordCommand;
import com.mes.security.auth.application.command.LockUserCommand;
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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * [Hexagonal Architecture: Inbound Adapter 測試]
 * [CQRS Pattern: Command Side 控制器測試]
 *
 * 使用 @WebMvcTest 測試 UserCommandController。
 * 使用 @WithMockUser 模擬不同角色。
 */
@DisplayName("UserCommandController 測試")
@WebMvcTest(UserCommandController.class)
@Import(SecurityConfig.class)
class UserCommandControllerTest {

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
    @DisplayName("PUT /api/v1/users/{id}/role - 指派角色")
    class AssignRoleTests {

        @Test
        @DisplayName("ADMIN 角色應可指派角色 (200)")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn200ForAdmin() throws Exception {
            AssignRoleCommand command = new AssignRoleCommand("user-123", "OPERATOR");

            mockMvc.perform(put("/api/v1/users/user-123/role")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("OPERATOR 角色不應可指派角色 (403)")
        @WithMockUser(roles = "OPERATOR")
        void shouldReturn403ForOperator() throws Exception {
            AssignRoleCommand command = new AssignRoleCommand("user-123", "VIEWER");

            mockMvc.perform(put("/api/v1/users/user-123/role")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/users/{id}/password - 變更密碼")
    class ChangePasswordTests {

        @Test
        @DisplayName("已認證使用者應可變更密碼 (200)")
        @WithMockUser(roles = "OPERATOR")
        void shouldReturn200ForAuthenticatedUser() throws Exception {
            ChangePasswordCommand command = new ChangePasswordCommand("user-123", "oldPass", "newPass");

            mockMvc.perform(put("/api/v1/users/user-123/password")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/users/{id}/lock - 鎖定使用者")
    class LockUserTests {

        @Test
        @DisplayName("ADMIN 角色應可鎖定使用者 (200)")
        @WithMockUser(roles = "ADMIN")
        void shouldReturn200ForAdmin() throws Exception {
            LockUserCommand command = new LockUserCommand("user-123", "Suspicious activity");

            mockMvc.perform(put("/api/v1/users/user-123/lock")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200));
        }

        @Test
        @DisplayName("VIEWER 角色不應可鎖定使用者 (403)")
        @WithMockUser(roles = "VIEWER")
        void shouldReturn403ForViewer() throws Exception {
            LockUserCommand command = new LockUserCommand("user-123", "Some reason");

            mockMvc.perform(put("/api/v1/users/user-123/lock")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isForbidden());
        }
    }
}
