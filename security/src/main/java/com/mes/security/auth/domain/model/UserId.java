package com.mes.security.auth.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.Identity;

import java.util.UUID;

/**
 * [DDD Pattern: Value Object - Identity]
 * [SOLID: SRP - 只負責使用者識別碼的封裝與產生]
 *
 * 將原始的 String ID 包裝為強型別的 UserId，
 * 避免與其他聚合的 ID 混淆。
 *
 * 使用 UUID 作為預設的 ID 產生策略。
 */
@ValueObject
public final class UserId extends Identity<String> {

    public UserId(String value) {
        super(value);
    }

    /**
     * 工廠方法：產生新的隨機 UserId。
     *
     * @return 新的 UserId 實例
     */
    public static UserId generate() {
        return new UserId(UUID.randomUUID().toString());
    }

    /**
     * 工廠方法：從既有的字串值建立 UserId。
     *
     * @param value 既有的 ID 字串
     * @return UserId 實例
     */
    public static UserId of(String value) {
        return new UserId(value);
    }
}
