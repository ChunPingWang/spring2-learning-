package com.mes.cloud.material.domain.service;

import com.mes.common.ddd.annotation.DomainService;
import com.mes.cloud.material.domain.Material;

/**
 * [DDD Pattern: Domain Service - 庫存領域服務]
 * [SOLID: SRP - 負責跨聚合或不屬於單一聚合的庫存計算邏輯]
 *
 * Domain Service 封裝不屬於任何單一 Aggregate Root 的領域邏輯。
 * 此服務提供庫存可用性檢查與補貨計算等跨領域的功能。
 *
 * 注意：Domain Service 不應有 Spring 相關依賴，
 * 它是純粹的領域邏輯，透過 @Configuration 註冊為 Bean。
 */
@DomainService
public class StockDomainService {

    /**
     * 檢查物料庫存是否足以滿足需求數量。
     *
     * @param material    要檢查的物料
     * @param requiredQty 需求數量
     * @return 若庫存充足回傳 true
     */
    public boolean checkStockAvailability(Material material, int requiredQty) {
        return material.getStockLevel().getCurrentQuantity() >= requiredQty;
    }

    /**
     * 計算建議的補貨數量。
     * 規則：2 倍最低庫存 - 當前庫存（若已高於 2 倍最低庫存則回傳 0）。
     *
     * @param material 要計算的物料
     * @return 建議的補貨數量
     */
    public int calculateReorderQuantity(Material material) {
        int targetLevel = material.getMinimumStock() * 2;
        int currentQuantity = material.getStockLevel().getCurrentQuantity();
        int reorderQty = targetLevel - currentQuantity;
        return Math.max(reorderQty, 0);
    }
}
