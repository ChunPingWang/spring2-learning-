package com.mes.security.auth.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;
import com.mes.common.exception.DomainException;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * [DDD Pattern: Value Object - 使用者名稱]
 * [SOLID: SRP - 只負責使用者名稱的封裝與驗證]
 *
 * Username 值物件，驗證：3-50 字元，僅允許英文字母、數字及底線。
 * 不可變物件，相等性由 value 決定。
 */
@ValueObject
public final class Username extends BaseValueObject {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9_]{3,50}$");

    private final String value;

    /**
     * 建構 Username，自動驗證格式。
     *
     * @param value 使用者名稱字串
     * @throws DomainException 若格式不合法
     */
    public Username(String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new DomainException("Username must not be blank");
        }
        if (!USERNAME_PATTERN.matcher(value).matches()) {
            throw new DomainException(
                    "Username must be 3-50 characters, alphanumeric and underscore only: " + value);
        }
        this.value = value;
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
