package com.mes.batch.domain.model;

import java.time.LocalDate;

public class ProductionRecord {
    private String orderId;
    private String lineId;
    private LocalDate productionDate;
    private Integer outputQuantity;
    private Integer defectiveQuantity;
    private String status;

    public ProductionRecord() {
    }

    public ProductionRecord(String orderId, String lineId, LocalDate productionDate, 
                           Integer outputQuantity, Integer defectiveQuantity, String status) {
        this.orderId = orderId;
        this.lineId = lineId;
        this.productionDate = productionDate;
        this.outputQuantity = outputQuantity;
        this.defectiveQuantity = defectiveQuantity;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getLineId() {
        return lineId;
    }

    public LocalDate getProductionDate() {
        return productionDate;
    }

    public Integer getOutputQuantity() {
        return outputQuantity;
    }

    public Integer getDefectiveQuantity() {
        return defectiveQuantity;
    }

    public String getStatus() {
        return status;
    }
}
