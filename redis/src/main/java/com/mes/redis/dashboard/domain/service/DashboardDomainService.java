package com.mes.redis.dashboard.domain.service;

import com.mes.common.ddd.annotation.DomainService;
import com.mes.redis.dashboard.domain.model.EquipmentStatusSnapshot;
import com.mes.redis.dashboard.domain.model.ProductionSummary;

import java.util.List;

/**
 * [DDD Pattern: Domain Service - 看板領域服務]
 * [SOLID: SRP - 封裝不屬於任何單一 Entity 或 Value Object 的看板計算邏輯]
 *
 * 看板領域服務，提供跨聚合的計算邏輯：
 * <ul>
 *   <li>計算產線效率（綜合生產摘要與設備狀態）</li>
 *   <li>判斷產線是否健康（無故障設備）</li>
 * </ul>
 *
 * 注意：此類別無任何 Spring 依賴，純粹的領域邏輯。
 */
@DomainService
public class DashboardDomainService {

    /**
     * 計算產線效率。
     * 計算公式：良率 * 運行設備比例。
     * 若無設備資訊，則直接回傳良率。
     *
     * @param summary  生產摘要
     * @param statuses 設備狀態快照列表
     * @return 產線效率（0.0 ~ 1.0）
     */
    public double calculateLineEfficiency(ProductionSummary summary,
                                          List<EquipmentStatusSnapshot> statuses) {
        double yieldRate = summary.getYieldRate().doubleValue();

        if (statuses == null || statuses.isEmpty()) {
            return yieldRate;
        }

        long runningCount = 0;
        for (EquipmentStatusSnapshot status : statuses) {
            if ("RUNNING".equals(status.getStatus())) {
                runningCount++;
            }
        }
        double runningRatio = (double) runningCount / statuses.size();

        return yieldRate * runningRatio;
    }

    /**
     * 判斷產線是否健康。
     * 健康的定義：沒有任何設備處於 BREAKDOWN 狀態。
     *
     * @param statuses 設備狀態快照列表
     * @return true 表示產線健康
     */
    public boolean isLineHealthy(List<EquipmentStatusSnapshot> statuses) {
        if (statuses == null || statuses.isEmpty()) {
            return true;
        }
        for (EquipmentStatusSnapshot status : statuses) {
            if ("BREAKDOWN".equals(status.getStatus())) {
                return false;
            }
        }
        return true;
    }
}
