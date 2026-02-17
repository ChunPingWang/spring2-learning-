package com.mes.security.auth.domain.model;

import com.mes.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [DDD Pattern: Value Object 單元測試]
 *
 * 測試 Role 值物件的行為：建立、權限檢查、相等性、預定義常數。
 */
@DisplayName("Role 值物件測試")
class RoleTest {

    @Test
    @DisplayName("使用合法參數建立角色應成功")
    void shouldCreateRoleWithValidParameters() {
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(new Permission("WORK_ORDER", "READ"));
        Role role = new Role("TESTER", "測試角色", permissions);

        assertThat(role.getName()).isEqualTo("TESTER");
        assertThat(role.getDescription()).isEqualTo("測試角色");
        assertThat(role.getPermissions()).hasSize(1);
    }

    @Test
    @DisplayName("角色名稱為空應拋出 DomainException")
    void shouldThrowWhenNameIsBlank() {
        assertThatThrownBy(new org.assertj.core.api.ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                new Role("", "desc", null);
            }
        }).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("擁有匹配權限的角色 hasPermission 應回傳 true")
    void shouldReturnTrueForMatchingPermission() {
        assertThat(Role.ROLE_ADMIN.hasPermission("WORK_ORDER", "READ")).isTrue();
        assertThat(Role.ROLE_ADMIN.hasPermission("USER", "DELETE")).isTrue();
    }

    @Test
    @DisplayName("沒有匹配權限的角色 hasPermission 應回傳 false")
    void shouldReturnFalseForNonMatchingPermission() {
        assertThat(Role.ROLE_VIEWER.hasPermission("WORK_ORDER", "DELETE")).isFalse();
    }

    @Test
    @DisplayName("相同名稱的角色應相等")
    void shouldBeEqualForSameName() {
        Set<Permission> permissions = new HashSet<Permission>();
        permissions.add(new Permission("WORK_ORDER", "READ"));
        Role role1 = new Role("ADMIN", "First", permissions);
        Role role2 = new Role("ADMIN", "Second", null);

        assertThat(role1).isEqualTo(role2);
        assertThat(role1.hashCode()).isEqualTo(role2.hashCode());
    }

    @Test
    @DisplayName("預定義常數應正確初始化")
    void shouldHavePredefinedConstants() {
        assertThat(Role.ROLE_ADMIN.getName()).isEqualTo("ADMIN");
        assertThat(Role.ROLE_OPERATOR.getName()).isEqualTo("OPERATOR");
        assertThat(Role.ROLE_VIEWER.getName()).isEqualTo("VIEWER");

        assertThat(Role.ROLE_ADMIN.getPermissions()).isNotEmpty();
        assertThat(Role.ROLE_OPERATOR.getPermissions()).isNotEmpty();
        assertThat(Role.ROLE_VIEWER.getPermissions()).isNotEmpty();
    }
}
