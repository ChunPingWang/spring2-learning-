package com.mes.security.auth.application.command.handler;

import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.security.auth.application.command.CreateUserCommand;
import com.mes.security.auth.application.port.PasswordEncoderPort;
import com.mes.security.auth.domain.model.User;
import com.mes.security.auth.domain.model.UserId;
import com.mes.security.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * [CQRS Pattern: Command Handler 單元測試]
 *
 * 測試 CreateUserCommandHandler 的業務邏輯，使用 mock Repository 和 PasswordEncoder。
 */
@DisplayName("CreateUserCommandHandler 測試")
class CreateUserCommandHandlerTest {

    private UserRepository userRepository;
    private PasswordEncoderPort passwordEncoder;
    private CreateUserCommandHandler handler;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoderPort.class);
        handler = new CreateUserCommandHandler(userRepository, passwordEncoder);
    }

    @Test
    @DisplayName("合法命令應成功建立使用者並回傳 ID")
    void shouldCreateUserSuccessfully() {
        // Arrange
        when(userRepository.existsByUsername("new_user")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded");

        CreateUserCommand command = new CreateUserCommand(
                "new_user", "password123", "new@example.com",
                Arrays.asList("OPERATOR"));

        // Act
        String userId = handler.handle(command);

        // Assert
        assertThat(userId).isNotNull().isNotEmpty();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());

        User savedUser = captor.getValue();
        assertThat(savedUser.getUsername().getValue()).isEqualTo("new_user");
        assertThat(savedUser.getEncodedPassword()).isEqualTo("$2a$10$encoded");
        assertThat(savedUser.getRoles()).hasSize(1);
    }

    @Test
    @DisplayName("重複使用者名稱應拋出 BusinessRuleViolationException")
    void shouldThrowWhenUsernameAlreadyExists() {
        // Arrange
        when(userRepository.existsByUsername("existing_user")).thenReturn(true);

        final CreateUserCommand command = new CreateUserCommand(
                "existing_user", "password123", "test@example.com", null);

        // Act & Assert
        assertThatThrownBy(new org.assertj.core.api.ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                handler.handle(command);
            }
        }).isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("already exists");

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    @DisplayName("不指定角色也應成功建立使用者")
    void shouldCreateUserWithoutRoles() {
        // Arrange
        when(userRepository.existsByUsername("no_role_user")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$10$encoded");

        CreateUserCommand command = new CreateUserCommand(
                "no_role_user", "password123", "norole@example.com", null);

        // Act
        String userId = handler.handle(command);

        // Assert
        assertThat(userId).isNotNull();

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRoles()).isEmpty();
    }

    @Test
    @DisplayName("getCommandType 應回傳 CreateUserCommand.class")
    void shouldReturnCorrectCommandType() {
        assertThat(handler.getCommandType()).isEqualTo(CreateUserCommand.class);
    }
}
