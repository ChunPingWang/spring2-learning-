package com.mes.redis.dashboard.application.query.dto;

import java.math.BigDecimal;

/**
 * [CQRS Pattern: Read Model DTO - 產線概覽視圖]
 * [SOLID: SRP - 只負責承載產線概覽的展示資料]
 *
 * 用於 Query 端回傳的產線概覽 DTO。
 * 提供精簡的產線運行狀態摘要。
 */
public class LineOverviewView {

    private String lineId;
    private int currentOutput;
    private BigDecimal yieldRate;
    private int runningEquipmentCount;
    private int totalEquipmentCount;

    public LineOverviewView() {
    }

    public LineOverviewView(String lineId, int currentOutput, BigDecimal yieldRate,
                            int runningEquipmentCount, int totalEquipmentCount) {
        this.lineId = lineId;
        this.currentOutput = currentOutput;
        this.yieldRate = yieldRate;
        this.runningEquipmentCount = runningEquipmentCount;
        this.totalEquipmentCount = totalEquipmentCount;
    }

    public String getLineId() {
        return lineId;
    }

    public void setLineId(String lineId) {
        this.lineId = lineId;
    }

    public int getCurrentOutput() {
        return currentOutput;
    }

    public void setCurrentOutput(int currentOutput) {
        this.currentOutput = currentOutput;
    }

    public BigDecimal getYieldRate() {
        return yieldRate;
    }

    public void setYieldRate(BigDecimal yieldRate) {
        this.yieldRate = yieldRate;
    }

    public int getRunningEquipmentCount() {
        return runningEquipmentCount;
    }

    public void setRunningEquipmentCount(int runningEquipmentCount) {
        this.runningEquipmentCount = runningEquipmentCount;
    }

    public int getTotalEquipmentCount() {
        return totalEquipmentCount;
    }

    public void setTotalEquipmentCount(int totalEquipmentCount) {
        this.totalEquipmentCount = totalEquipmentCount;
    }
}
