package com.mes.cloud.material.domain;

import com.mes.common.ddd.model.Identity;

import java.util.UUID;

/**
 * [DDD Pattern: Identity Value Object - 物料唯一識別]
 * [SOLID: SRP - 只負責物料 ID 的建構與封裝]
 *
 * 將原始的 String ID 包裝為強型別的 MaterialId，
 * 避免與其他聚合的 ID（如 WorkOrderId）混淆。
 *
 * 使用工廠方法模式提供兩種建構方式：
 * - generate(): 自動產生 UUID
 * - of(String): 從已知值建構
 */
public class MaterialId extends Identity<String> {

    private MaterialId(String value) {
        super(value);
    }

    /**
     * 自動產生一個新的 MaterialId（使用 UUID）。
     *
     * @return 新的 MaterialId
     */
    public static MaterialId generate() {
        return new MaterialId("MAT-" + UUID.randomUUID().toString().substring(0, 8));
    }

    /**
     * 從已知的 ID 值建構 MaterialId。
     *
     * @param value ID 值
     * @return MaterialId 實例
     */
    public static MaterialId of(String value) {
        return new MaterialId(value);
    }
}
