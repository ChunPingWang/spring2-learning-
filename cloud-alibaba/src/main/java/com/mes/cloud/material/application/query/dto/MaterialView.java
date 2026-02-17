package com.mes.cloud.material.application.query.dto;

import java.time.LocalDateTime;

/**
 * [CQRS Pattern: Read Model DTO - 物料檢視]
 * [SOLID: SRP - 只負責呈現物料的讀取資料]
 *
 * 用於查詢端的資料傳輸物件，
 * 提供前端展示所需的扁平化資料結構。
 */
public class MaterialView {

    private String id;
    private String name;
    private String type;
    private String typeName;
    private int stockQuantity;
    private String unit;
    private String supplierName;
    private boolean lowStock;
    private LocalDateTime createdAt;

    public MaterialView() {
    }

    // ========== Getters and Setters ==========

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public boolean isLowStock() {
        return lowStock;
    }

    public void setLowStock(boolean lowStock) {
        this.lowStock = lowStock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
