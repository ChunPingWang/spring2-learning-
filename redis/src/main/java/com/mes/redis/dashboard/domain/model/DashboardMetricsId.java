package com.mes.redis.dashboard.domain.model;

import com.mes.common.ddd.model.Identity;

/**
 * [DDD Pattern: Identity Value Object - 看板指標聚合根識別碼]
 * [SOLID: SRP - 只負責看板指標的唯一識別]
 *
 * 複合鍵模式：{lineId}:{yyyyMMdd}
 * 將原始 String ID 包裝為強型別，避免與其他聚合的 ID 混淆。
 */
public class DashboardMetricsId extends Identity<String> {

    public DashboardMetricsId(String value) {
        super(value);
    }

    /**
     * 從產線 ID 與日期字串建立複合鍵。
     *
     * @param lineId   產線 ID
     * @param dateStr  日期字串（yyyyMMdd）
     * @return DashboardMetricsId
     */
    public static DashboardMetricsId of(String lineId, String dateStr) {
        return new DashboardMetricsId(lineId + ":" + dateStr);
    }
}
