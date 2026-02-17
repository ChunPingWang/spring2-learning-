package com.mes.cloud.material.application.command;

import com.mes.common.cqrs.Command;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * [CQRS Pattern: Command - 物料入庫]
 * [SOLID: SRP - 只封裝物料入庫所需的資料]
 *
 * 代表「將物料入庫」的意圖。
 */
public class ReceiveMaterialCommand implements Command {

    @NotBlank(message = "物料 ID 不可為空")
    private String materialId;

    @NotNull(message = "入庫數量不可為空")
    @Min(value = 1, message = "入庫數量必須大於零")
    private Integer quantity;

    private String supplierId;

    public ReceiveMaterialCommand() {
    }

    public ReceiveMaterialCommand(String materialId, int quantity, String supplierId) {
        this.materialId = materialId;
        this.quantity = quantity;
        this.supplierId = supplierId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public void setMaterialId(String materialId) {
        this.materialId = materialId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
}
