package com.mes.security.auth.domain.service;

import com.mes.security.auth.domain.model.Email;
import com.mes.security.auth.domain.model.Role;
import com.mes.security.auth.domain.model.User;
import com.mes.security.auth.domain.model.UserId;
import com.mes.security.auth.domain.model.Username;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * [DDD Pattern: Domain Service 單元測試]
 *
 * 測試 AuthenticationDomainService 的權限檢查邏輯。
 */
@DisplayName("AuthenticationDomainService 測試")
class AuthenticationDomainServiceTest {

    private AuthenticationDomainService service;
    private User activeUser;

    @BeforeEach
    void setUp() {
        service = new AuthenticationDomainService();
        activeUser = new User(
                UserId.generate(),
                new Username("test_user"),
                "$2a$10$encodedPassword",
                new Email("test@example.com")
        );
        activeUser.assignRole(Role.ROLE_OPERATOR);
    }

    @Test
    @DisplayName("啟用且未鎖定的使用者擁有對應權限應回傳 true")
    void shouldReturnTrueForActiveUserWithPermission() {
        assertThat(service.hasPermission(activeUser, "WORK_ORDER", "READ")).isTrue();
    }

    @Test
    @DisplayName("使用者沒有對應權限應回傳 false")
    void shouldReturnFalseWhenUserLacksPermission() {
        assertThat(service.hasPermission(activeUser, "USER", "DELETE")).isFalse();
    }

    @Test
    @DisplayName("被鎖定的使用者應回傳 false")
    void shouldReturnFalseForLockedUser() {
        activeUser.lock("Test");

        assertThat(service.hasPermission(activeUser, "WORK_ORDER", "READ")).isFalse();
    }

    @Test
    @DisplayName("null 使用者應回傳 false")
    void shouldReturnFalseForNullUser() {
        assertThat(service.hasPermission(null, "WORK_ORDER", "READ")).isFalse();
    }
}
