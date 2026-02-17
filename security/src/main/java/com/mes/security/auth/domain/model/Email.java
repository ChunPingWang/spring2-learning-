package com.mes.security.auth.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;
import com.mes.common.exception.DomainException;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * [DDD Pattern: Value Object - 電子郵件]
 * [SOLID: SRP - 只負責電子郵件格式的封裝與驗證]
 *
 * Email 值物件，自我驗證電子郵件格式。
 * 不可變物件，相等性由 value 決定。
 */
@ValueObject
public final class Email extends BaseValueObject {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private final String value;

    /**
     * 建構 Email，自動驗證格式。
     *
     * @param value 電子郵件字串
     * @throws DomainException 若格式不合法
     */
    public Email(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("Email must not be blank");
        }
        if (!EMAIL_PATTERN.matcher(value).matches()) {
            throw new DomainException("Invalid email format: " + value);
        }
        this.value = value.toLowerCase();
    }

    public String getValue() {
        return value;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.<Object>asList(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
