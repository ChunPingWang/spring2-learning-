package com.mes.cloud.material.domain;

/**
 * [DDD Pattern: Enum Value Object - 物料類型]
 * [SOLID: OCP - 新增物料類型只需新增列舉常數]
 *
 * 定義 MES 系統中物料的分類。
 * 每種類型附帶中文描述，用於展示層呈現。
 */
public enum MaterialType {

    /** 原物料 — 生產用的原始材料 */
    RAW_MATERIAL("原物料"),

    /** 半成品 — 加工中的中間產物 */
    SEMI_FINISHED("半成品"),

    /** 零組件 — 組裝用的標準零件 */
    COMPONENT("零組件"),

    /** 耗材 — 生產過程中消耗的輔助材料 */
    CONSUMABLE("耗材");

    private final String description;

    MaterialType(String description) {
        this.description = description;
    }

    /**
     * 取得物料類型的中文描述。
     *
     * @return 中文描述
     */
    public String getDescription() {
        return description;
    }
}
