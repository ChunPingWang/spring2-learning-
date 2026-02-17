package com.mes.security.auth.domain.model;

import com.mes.common.exception.DomainException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [DDD Pattern: Value Object 單元測試]
 *
 * 測試 Email 值物件的行為：合法格式、非法格式、相等性。
 */
@DisplayName("Email 值物件測試")
class EmailTest {

    @Test
    @DisplayName("合法的電子郵件格式應建立成功")
    void shouldCreateWithValidEmail() {
        Email email = new Email("user@example.com");

        assertThat(email.getValue()).isEqualTo("user@example.com");
    }

    @Test
    @DisplayName("空白電子郵件應拋出 DomainException")
    void shouldThrowWhenBlank() {
        assertThatThrownBy(new org.assertj.core.api.ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                new Email("");
            }
        }).isInstanceOf(DomainException.class)
                .hasMessageContaining("blank");
    }

    @Test
    @DisplayName("缺少 @ 符號的電子郵件應拋出 DomainException")
    void shouldThrowWhenMissingAtSign() {
        assertThatThrownBy(new org.assertj.core.api.ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                new Email("userexample.com");
            }
        }).isInstanceOf(DomainException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    @DisplayName("缺少網域的電子郵件應拋出 DomainException")
    void shouldThrowWhenMissingDomain() {
        assertThatThrownBy(new org.assertj.core.api.ThrowableAssert.ThrowingCallable() {
            @Override
            public void call() {
                new Email("user@");
            }
        }).isInstanceOf(DomainException.class)
                .hasMessageContaining("Invalid email format");
    }

    @Test
    @DisplayName("相同值的電子郵件應相等")
    void shouldBeEqualForSameValue() {
        Email email1 = new Email("User@Example.COM");
        Email email2 = new Email("user@example.com");

        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
    }
}
