package com.mes.security.auth.domain.model;

import com.mes.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [DDD Pattern: Value Object 單元測試]
 *
 * 測試 Permission 值物件的行為：建立、匹配、相等性。
 */
@DisplayName("Permission 值物件測試")
class PermissionTest {

    @Test
    @DisplayName("使用合法參數建立權限應成功")
    void shouldCreatePermissionWithValidParameters() {
        Permission permission = new Permission("WORK_ORDER", "READ");

        assertThat(permission.getResource()).isEqualTo("WORK_ORDER");
        assertThat(permission.getAction()).isEqualTo("READ");
    }

    @Test
    @DisplayName("資源為空應拋出 DomainException")
    void shouldThrowWhenResourceIsBlank() {
        assertThatThrownBy(new org.assertj.core.api.ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                new Permission("", "READ");
            }
        }).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("動作為空應拋出 DomainException")
    void shouldThrowWhenActionIsBlank() {
        assertThatThrownBy(new org.assertj.core.api.ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                new Permission("WORK_ORDER", "");
            }
        }).isInstanceOf(DomainException.class);
    }

    @Test
    @DisplayName("匹配相同資源和動作應回傳 true")
    void shouldMatchWhenResourceAndActionMatch() {
        Permission permission = new Permission("WORK_ORDER", "READ");

        assertThat(permission.matches("WORK_ORDER", "READ")).isTrue();
        assertThat(permission.matches("work_order", "read")).isTrue();
    }

    @Test
    @DisplayName("相同資源和動作的權限應相等")
    void shouldBeEqualForSameResourceAndAction() {
        Permission p1 = new Permission("WORK_ORDER", "READ");
        Permission p2 = new Permission("work_order", "read");

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }
}
