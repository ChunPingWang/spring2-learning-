package com.mes.redis.dashboard.application.query.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * [CQRS Pattern: Read Model DTO - 看板指標視圖]
 * [SOLID: SRP - 只負責承載看板指標的展示資料]
 *
 * 用於 Query 端回傳的看板指標 DTO。
 * 與領域模型解耦，專為 API 回應設計。
 */
public class DashboardView {

    private String lineId;
    private int totalOutput;
    private int goodCount;
    private int defectCount;
    private BigDecimal yieldRate;
    private double throughputPerHour;
    private List<EquipmentStatusView> equipmentStatuses;
    private LocalDateTime lastUpdated;

    public DashboardView() {
    }

    public DashboardView(String lineId, int totalOutput, int goodCount, int defectCount,
                         BigDecimal yieldRate, double throughputPerHour,
                         List<EquipmentStatusView> equipmentStatuses, LocalDateTime lastUpdated) {
        this.lineId = lineId;
        this.totalOutput = totalOutput;
        this.goodCount = goodCount;
        this.defectCount = defectCount;
        this.yieldRate = yieldRate;
        this.throughputPerHour = throughputPerHour;
        this.equipmentStatuses = equipmentStatuses;
        this.lastUpdated = lastUpdated;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public int getTotalOutput() {
        return totalOutput;
    }

    public void setTotalOutput(int totalOutput) {
        this.totalOutput = totalOutput;
    }

    public int getGoodCount() {
        return goodCount;
    }

    public void setGoodCount(int goodCount) {
        this.goodCount = goodCount;
    }

    public int getDefectCount() {
        return defectCount;
    }

    public void setDefectCount(int defectCount) {
        this.defectCount = defectCount;
    }

    public BigDecimal getYieldRate() {
        return yieldRate;
    }

    public void setYieldRate(BigDecimal yieldRate) {
        this.yieldRate = yieldRate;
    }

    public double getThroughputPerHour() {
        return throughputPerHour;
    }

    public void setThroughputPerHour(double throughputPerHour) {
        this.throughputPerHour = throughputPerHour;
    }

    public List<EquipmentStatusView> getEquipmentStatuses() {
        return equipmentStatuses;
    }

    public void setEquipmentStatuses(List<EquipmentStatusView> equipmentStatuses) {
        this.equipmentStatuses = equipmentStatuses;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    /**
     * [CQRS Pattern: 內嵌 DTO - 設備狀態視圖]
     */
    public static class EquipmentStatusView {

        private String equipmentId;
        private String equipmentName;
        private String status;
        private LocalDateTime lastUpdated;

        public EquipmentStatusView() {
        }

        public EquipmentStatusView(String equipmentId, String equipmentName,
                                   String status, LocalDateTime lastUpdated) {
            this.equipmentId = equipmentId;
            this.equipmentName = equipmentName;
            this.status = status;
            this.lastUpdated = lastUpdated;
        }

        public String getEquipmentId() {
            return equipmentId;
        }

        public void setEquipmentId(String equipmentId) {
            this.equipmentId = equipmentId;
        }

        public String getEquipmentName() {
            return equipmentName;
        }

        public void setEquipmentName(String equipmentName) {
            this.equipmentName = equipmentName;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public LocalDateTime getLastUpdated() {
            return lastUpdated;
        }

        public void setLastUpdated(LocalDateTime lastUpdated) {
            this.lastUpdated = lastUpdated;
        }
    }
}
