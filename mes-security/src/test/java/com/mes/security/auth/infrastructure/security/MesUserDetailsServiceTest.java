package com.mes.security.auth.infrastructure.security;

import com.mes.security.auth.domain.model.Email;
import com.mes.security.auth.domain.model.Role;
import com.mes.security.auth.domain.model.User;
import com.mes.security.auth.domain.model.UserId;
import com.mes.security.auth.domain.model.Username;
import com.mes.security.auth.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * [Hexagonal Architecture: Adapter 單元測試]
 *
 * 測試 MesUserDetailsService 將領域 User 轉換為 Spring Security UserDetails。
 */
@DisplayName("MesUserDetailsService 測試")
class MesUserDetailsServiceTest {

    private UserRepository userRepository;
    private MesUserDetailsService service;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        service = new MesUserDetailsService(userRepository);
    }

    @Test
    @DisplayName("載入存在的使用者應回傳正確的 UserDetails")
    void shouldLoadExistingUser() {
        // Arrange
        User user = new User(
                UserId.generate(),
                new Username("admin_user"),
                "$2a$10$encodedPassword",
                new Email("admin@example.com")
        );
        user.assignRole(Role.ROLE_ADMIN);
        when(userRepository.findByUsername("admin_user")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = service.loadUserByUsername("admin_user");

        // Assert
        assertThat(userDetails.getUsername()).isEqualTo("admin_user");
        assertThat(userDetails.getPassword()).isEqualTo("$2a$10$encodedPassword");
        assertThat(userDetails.isEnabled()).isTrue();
        assertThat(userDetails.isAccountNonLocked()).isTrue();
        assertThat(userDetails.getAuthorities()).isNotEmpty();
    }

    @Test
    @DisplayName("載入不存在的使用者應拋出 UsernameNotFoundException")
    void shouldThrowWhenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("nonexistent")).thenReturn(Optional.<User>empty());

        // Act & Assert
        assertThatThrownBy(new org.assertj.core.api.ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                service.loadUserByUsername("nonexistent");
            }
        }).isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("nonexistent");
    }

    @Test
    @DisplayName("被鎖定的使用者應回傳 accountNonLocked=false")
    void shouldReturnLockedStatusForLockedUser() {
        // Arrange
        User user = new User(
                UserId.generate(),
                new Username("locked_user"),
                "$2a$10$encodedPassword",
                new Email("locked@example.com")
        );
        user.lock("Test lock");
        when(userRepository.findByUsername("locked_user")).thenReturn(Optional.of(user));

        // Act
        UserDetails userDetails = service.loadUserByUsername("locked_user");

        // Assert
        assertThat(userDetails.isAccountNonLocked()).isFalse();
    }
}
