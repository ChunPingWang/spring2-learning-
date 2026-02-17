package com.mes.batch.domain.model;

import java.time.LocalDate;

public class DailyProductionReport {
    private Long id;
    private LocalDate reportDate;
    private String lineId;
    private Integer totalOrders;
    private Integer completedOrders;
    private Integer totalOutput;
    private Integer defectiveOutput;
    private Double yieldRate;
    private String generatedAt;

    public DailyProductionReport() {
    }

    public DailyProductionReport(LocalDate reportDate, String lineId) {
        this.reportDate = reportDate;
        this.lineId = lineId;
    }

    public void updateMetrics(int totalOrders, int completedOrders, int totalOutput, int defectiveOutput) {
        this.totalOrders = totalOrders;
        this.completedOrders = completedOrders;
        this.totalOutput = totalOutput;
        this.defectiveOutput = defectiveOutput;
        this.yieldRate = totalOutput > 0 ? (double) (totalOutput - defectiveOutput) / totalOutput : 0.0;
    }

    public Long getId() {
        return id;
    }

    public LocalDate getReportDate() {
        return reportDate;
    }

    public String getLineId() {
        return lineId;
    }

    public Integer getTotalOrders() {
        return totalOrders;
    }

    public Integer getCompletedOrders() {
        return completedOrders;
    }

    public Integer getTotalOutput() {
        return totalOutput;
    }

    public Integer getDefectiveOutput() {
        return defectiveOutput;
    }

    public Double getYieldRate() {
        return yieldRate;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }
}
