package com.mes.cloud.material.application.command;

import com.mes.common.cqrs.Command;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * [CQRS Pattern: Command - 物料消耗]
 * [SOLID: SRP - 只封裝物料消耗所需的資料]
 *
 * 代表「消耗物料（用於生產工單）」的意圖。
 */
public class ConsumeMaterialCommand implements Command {

    @NotBlank(message = "物料 ID 不可為空")
    private String materialId;

    @NotNull(message = "消耗數量不可為空")
    @Min(value = 1, message = "消耗數量必須大於零")
    private Integer quantity;

    @NotBlank(message = "工單 ID 不可為空")
    private String workOrderId;

    public ConsumeMaterialCommand() {
    }

    public ConsumeMaterialCommand(String materialId, int quantity, String workOrderId) {
        this.materialId = materialId;
        this.quantity = quantity;
        this.workOrderId = workOrderId;
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

    public String getWorkOrderId() {
        return workOrderId;
    }

    public void setWorkOrderId(String workOrderId) {
        this.workOrderId = workOrderId;
    }
}
