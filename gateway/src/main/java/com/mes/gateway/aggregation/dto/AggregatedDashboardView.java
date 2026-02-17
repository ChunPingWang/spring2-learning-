package com.mes.gateway.aggregation.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * [DDD Pattern: View Model - 聚合儀表板檢視]
 * [SOLID: SRP - 只負責承載聚合後的儀表板資料]
 *
 * 由閘道層聚合多個下游服務的回應後組成的統一檢視物件。
 * 包含生產摘要、設備狀態等來自不同微服務的資料。
 *
 * <h3>教學重點</h3>
 * <ul>
 *     <li>API 閘道聚合模式（Gateway Aggregation Pattern）</li>
 *     <li>減少客戶端多次呼叫，由閘道統一聚合</li>
 *     <li>使用 Map 作為動態資料容器，適應下游服務回應變化</li>
 * </ul>
 */
public class AggregatedDashboardView {

    /** 生產摘要資料，來自 mes-web-api */
    private Map<String, Object> productionSummary;

    /** 設備狀態資料，來自 mes-mybatis */
    private Map<String, Object> equipmentStatus;

    /** 聚合時間戳 */
    private String timestamp;

    public AggregatedDashboardView() {
    }

    public AggregatedDashboardView(Map<String, Object> productionSummary,
                                   Map<String, Object> equipmentStatus,
                                   String timestamp) {
        this.productionSummary = productionSummary;
        this.equipmentStatus = equipmentStatus;
        this.timestamp = timestamp;
    }

    public Map<String, Object> getProductionSummary() {
        return productionSummary;
    }

    public void setProductionSummary(Map<String, Object> productionSummary) {
        this.productionSummary = productionSummary;
    }

    public Map<String, Object> getEquipmentStatus() {
        return equipmentStatus;
    }

    public void setEquipmentStatus(Map<String, Object> equipmentStatus) {
        this.equipmentStatus = equipmentStatus;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
