package com.mes.security.auth.adapter.in.web;

import com.mes.common.cqrs.CommandBus;
import com.mes.common.cqrs.QueryBus;
import com.mes.security.auth.application.port.PasswordEncoderPort;
import com.mes.security.auth.application.query.dto.UserView;
import com.mes.security.auth.infrastructure.security.JwtTokenProvider;
import com.mes.security.auth.infrastructure.security.SecurityConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * [Hexagonal Architecture: Inbound Adapter 測試]
 * [CQRS Pattern: Query Side 控制器測試]
 *
 * 使用 @WebMvcTest 測試 UserQueryController。
 */
@DisplayName("UserQueryController 測試")
@WebMvcTest(UserQueryController.class)
@Import(SecurityConfig.class)
class UserQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

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

    @Test
    @DisplayName("ADMIN 角色查詢使用者應回傳 200")
    @WithMockUser(roles = "ADMIN")
    void shouldReturn200ForAdminGetUser() throws Exception {
        // Arrange
        UserView userView = new UserView(
                "user-123", "testuser", "test@example.com",
                Arrays.asList("OPERATOR"), true, false, LocalDateTime.now());
        when(queryBus.dispatch(any())).thenReturn(userView);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/user-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.username").value("testuser"));
    }

    @Test
    @DisplayName("OPERATOR 角色依角色列出使用者應回傳 200")
    @WithMockUser(roles = "OPERATOR")
    void shouldReturn200ForOperatorListByRole() throws Exception {
        // Arrange
        UserView userView = new UserView(
                "user-1", "operator1", "op1@example.com",
                Arrays.asList("OPERATOR"), true, false, LocalDateTime.now());
        List<UserView> users = Collections.singletonList(userView);
        when(queryBus.dispatch(any())).thenReturn(users);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users").param("role", "OPERATOR"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data[0].username").value("operator1"));
    }

    @Test
    @DisplayName("未認證的請求應回傳 401")
    void shouldReturn401ForUnauthenticatedRequest() throws Exception {
        mockMvc.perform(get("/api/v1/users/user-123"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("VIEWER 角色不應可查詢使用者 (403)")
    @WithMockUser(roles = "VIEWER")
    void shouldReturn403ForViewer() throws Exception {
        mockMvc.perform(get("/api/v1/users/user-123"))
                .andExpect(status().isForbidden());
    }
}
