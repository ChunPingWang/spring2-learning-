package com.mes.cloud.material.application.query.dto;

/**
 * [CQRS Pattern: Read Model DTO - 低庫存預警檢視]
 * [SOLID: SRP - 只負責呈現低庫存預警的讀取資料]
 *
 * 用於查詢端的資料傳輸物件，
 * 提供低庫存物料的詳細資訊包含缺口數量。
 */
public class StockAlertView {

    private String materialId;
    private String materialName;
    private int currentStock;
    private int minimumStock;
    private int deficit;

    public StockAlertView() {
    }

    public StockAlertView(String materialId, String materialName,
                           int currentStock, int minimumStock) {
        this.materialId = materialId;
        this.materialName = materialName;
        this.currentStock = currentStock;
        this.minimumStock = minimumStock;
        this.deficit = minimumStock - currentStock;
    }

    // ========== Getters and Setters ==========

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public void setMaterialName(String materialName) {
        this.materialName = materialName;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public int getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(int minimumStock) {
        this.minimumStock = minimumStock;
    }

    public int getDeficit() {
        return deficit;
    }

    public void setDeficit(int deficit) {
        this.deficit = deficit;
    }
}
