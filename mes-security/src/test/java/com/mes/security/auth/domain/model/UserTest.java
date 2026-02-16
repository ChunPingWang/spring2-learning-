package com.mes.security.auth.domain.model;

import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.DomainException;
import com.mes.security.auth.domain.event.RoleAssignedEvent;
import com.mes.security.auth.domain.event.UserCreatedEvent;
import com.mes.security.auth.domain.event.UserLockedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [DDD Pattern: Aggregate Root 單元測試]
 *
 * 測試 User 聚合根的行為，包括：角色管理、密碼變更、帳號鎖定、權限檢查、領域事件。
 */
@DisplayName("User 聚合根測試")
class UserTest {

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(
                UserId.generate(),
                new Username("test_user"),
                "$2a$10$encodedPassword",
                new Email("test@example.com")
        );
    }

    @Nested
    @DisplayName("建立使用者")
    class CreationTests {

        @Test
        @DisplayName("使用合法參數建立使用者應成功")
        void shouldCreateUserWithValidParameters() {
            assertThat(user.getUsername().getValue()).isEqualTo("test_user");
            assertThat(user.getEmail().getValue()).isEqualTo("test@example.com");
            assertThat(user.isEnabled()).isTrue();
            assertThat(user.isLocked()).isFalse();
            assertThat(user.getRoles()).isEmpty();
        }

        @Test
        @DisplayName("建立使用者應產生 UserCreatedEvent")
        void shouldRegisterUserCreatedEvent() {
            assertThat(user.getDomainEvents()).hasSize(1);
            assertThat(user.getDomainEvents().get(0)).isInstanceOf(UserCreatedEvent.class);

            UserCreatedEvent event = (UserCreatedEvent) user.getDomainEvents().get(0);
            assertThat(event.getUsername()).isEqualTo("test_user");
            assertThat(event.getEmail()).isEqualTo("test@example.com");
        }

        @Test
        @DisplayName("使用者名稱為 null 應拋出 DomainException")
        void shouldThrowWhenUsernameIsNull() {
            assertThatThrownBy(new org.assertj.core.api.ThrowableAssert.ThrowingCallable() {
                @Override
                public void call() {
                    new User(UserId.generate(), null, "encoded", new Email("a@b.com"));
                }
            }).isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("編碼密碼為空應拋出 DomainException")
        void shouldThrowWhenEncodedPasswordIsBlank() {
            assertThatThrownBy(new org.assertj.core.api.ThrowableAssert.ThrowingCallable() {
                @Override
                public void call() {
                    new User(UserId.generate(), new Username("abc"), "", new Email("a@b.com"));
                }
            }).isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("角色管理")
    class RoleManagementTests {

        @Test
        @DisplayName("指派角色應成功新增角色")
        void shouldAssignRoleSuccessfully() {
            user.assignRole(Role.ROLE_ADMIN);

            assertThat(user.getRoles()).hasSize(1);
            assertThat(user.getRoles()).contains(Role.ROLE_ADMIN);
        }

        @Test
        @DisplayName("指派角色應產生 RoleAssignedEvent")
        void shouldRegisterRoleAssignedEvent() {
            user.clearEvents();
            user.assignRole(Role.ROLE_OPERATOR);

            assertThat(user.getDomainEvents()).hasSize(1);
            assertThat(user.getDomainEvents().get(0)).isInstanceOf(RoleAssignedEvent.class);

            RoleAssignedEvent event = (RoleAssignedEvent) user.getDomainEvents().get(0);
            assertThat(event.getRoleName()).isEqualTo("OPERATOR");
        }

        @Test
        @DisplayName("重複指派角色應拋出 BusinessRuleViolationException")
        void shouldThrowWhenAssigningDuplicateRole() {
            user.assignRole(Role.ROLE_ADMIN);

            assertThatThrownBy(new org.assertj.core.api.ThrowableAssert.ThrowingCallable() {
                @Override
                public void call() {
                    user.assignRole(Role.ROLE_ADMIN);
                }
            }).isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("already has role");
        }

        @Test
        @DisplayName("移除存在的角色應成功")
        void shouldRemoveExistingRole() {
            user.assignRole(Role.ROLE_ADMIN);
            user.assignRole(Role.ROLE_OPERATOR);

            user.removeRole("ADMIN");

            assertThat(user.getRoles()).hasSize(1);
            assertThat(user.getRoles()).contains(Role.ROLE_OPERATOR);
        }

        @Test
        @DisplayName("移除不存在的角色應拋出 BusinessRuleViolationException")
        void shouldThrowWhenRemovingNonExistingRole() {
            assertThatThrownBy(new org.assertj.core.api.ThrowableAssert.ThrowingCallable() {
                @Override
                public void call() {
                    user.removeRole("ADMIN");
                }
            }).isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("does not have role");
        }
    }

    @Nested
    @DisplayName("密碼管理")
    class PasswordManagementTests {

        @Test
        @DisplayName("變更密碼應更新編碼密碼")
        void shouldChangePassword() {
            user.changePassword("$2a$10$newEncodedPassword");

            assertThat(user.getEncodedPassword()).isEqualTo("$2a$10$newEncodedPassword");
        }

        @Test
        @DisplayName("變更密碼為空應拋出 DomainException")
        void shouldThrowWhenNewPasswordIsBlank() {
            assertThatThrownBy(new org.assertj.core.api.ThrowableAssert.ThrowingCallable() {
                @Override
                public void call() {
                    user.changePassword("");
                }
            }).isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("帳號鎖定")
    class LockingTests {

        @Test
        @DisplayName("鎖定未鎖定的帳號應成功")
        void shouldLockUnlockedAccount() {
            user.lock("Suspicious activity");

            assertThat(user.isLocked()).isTrue();
        }

        @Test
        @DisplayName("鎖定帳號應產生 UserLockedEvent")
        void shouldRegisterUserLockedEvent() {
            user.clearEvents();
            user.lock("Suspicious activity");

            assertThat(user.getDomainEvents()).hasSize(1);
            assertThat(user.getDomainEvents().get(0)).isInstanceOf(UserLockedEvent.class);

            UserLockedEvent event = (UserLockedEvent) user.getDomainEvents().get(0);
            assertThat(event.getReason()).isEqualTo("Suspicious activity");
        }

        @Test
        @DisplayName("重複鎖定應拋出 BusinessRuleViolationException")
        void shouldThrowWhenLockingAlreadyLockedAccount() {
            user.lock("First lock");

            assertThatThrownBy(new org.assertj.core.api.ThrowableAssert.ThrowingCallable() {
                @Override
                public void call() {
                    user.lock("Second lock");
                }
            }).isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("already locked");
        }

        @Test
        @DisplayName("解鎖已鎖定的帳號應成功")
        void shouldUnlockLockedAccount() {
            user.lock("Test lock");
            user.unlock();

            assertThat(user.isLocked()).isFalse();
        }

        @Test
        @DisplayName("解鎖未鎖定的帳號應拋出 BusinessRuleViolationException")
        void shouldThrowWhenUnlockingNonLockedAccount() {
            assertThatThrownBy(new org.assertj.core.api.ThrowableAssert.ThrowingCallable() {
                @Override
                public void call() {
                    user.unlock();
                }
            }).isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("not locked");
        }
    }

    @Nested
    @DisplayName("權限檢查")
    class PermissionTests {

        @Test
        @DisplayName("擁有對應權限的角色應回傳 true")
        void shouldReturnTrueWhenHasPermission() {
            user.assignRole(Role.ROLE_OPERATOR);

            assertThat(user.hasPermission("WORK_ORDER", "READ")).isTrue();
            assertThat(user.hasPermission("WORK_ORDER", "WRITE")).isTrue();
        }

        @Test
        @DisplayName("沒有對應權限應回傳 false")
        void shouldReturnFalseWhenNoPermission() {
            user.assignRole(Role.ROLE_VIEWER);

            assertThat(user.hasPermission("WORK_ORDER", "DELETE")).isFalse();
        }

        @Test
        @DisplayName("沒有任何角色應回傳 false")
        void shouldReturnFalseWhenNoRoles() {
            assertThat(user.hasPermission("WORK_ORDER", "READ")).isFalse();
        }
    }
}
