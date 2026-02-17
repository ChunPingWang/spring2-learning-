package com.mes.cloud.material.application.command;

import com.mes.common.cqrs.Command;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * [CQRS Pattern: Command - 註冊新物料]
 * [SOLID: SRP - 只封裝註冊物料所需的資料]
 *
 * 代表「註冊一筆新物料」的意圖。
 * Command 是資料容器，不包含任何業務邏輯。
 */
public class RegisterMaterialCommand implements Command {

    @NotBlank(message = "物料名稱不可為空")
    private String name;

    @NotBlank(message = "物料類型不可為空")
    private String materialType;

    @NotBlank(message = "單位代碼不可為空")
    private String unitCode;

    @NotBlank(message = "單位名稱不可為空")
    private String unitName;

    @NotNull(message = "初始庫存不可為空")
    @Min(value = 0, message = "初始庫存不可為負數")
    private Integer initialStock;

    @NotNull(message = "最低庫存不可為空")
    @Min(value = 0, message = "最低庫存不可為負數")
    private Integer minimumStock;

    @NotBlank(message = "供應商 ID 不可為空")
    private String supplierId;

    @NotBlank(message = "供應商名稱不可為空")
    private String supplierName;

    private String contactInfo;

    public RegisterMaterialCommand() {
    }

    public RegisterMaterialCommand(String name, String materialType, String unitCode,
                                    String unitName, int initialStock, int minimumStock,
                                    String supplierId, String supplierName, String contactInfo) {
        this.name = name;
        this.materialType = materialType;
        this.unitCode = unitCode;
        this.unitName = unitName;
        this.initialStock = initialStock;
        this.minimumStock = minimumStock;
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.contactInfo = contactInfo;
    }

    // ========== Getters and Setters ==========

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMaterialType() {
        return materialType;
    }

    public void setMaterialType(String materialType) {
        this.materialType = materialType;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode(String unitCode) {
        this.unitCode = unitCode;
    }

    public String getUnitName() {
        return unitName;
    }

    public void setUnitName(String unitName) {
        this.unitName = unitName;
    }

    public Integer getInitialStock() {
        return initialStock;
    }

    public void setInitialStock(Integer initialStock) {
        this.initialStock = initialStock;
    }

    public Integer getMinimumStock() {
        return minimumStock;
    }

    public void setMinimumStock(Integer minimumStock) {
        this.minimumStock = minimumStock;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(String contactInfo) {
        this.contactInfo = contactInfo;
    }
}
