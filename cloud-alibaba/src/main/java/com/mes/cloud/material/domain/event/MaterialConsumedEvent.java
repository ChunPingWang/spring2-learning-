package com.mes.cloud.material.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event - 物料已消耗]
 * [SOLID: SRP - 只描述「物料已消耗」這一事實]
 *
 * 當物料透過 consume() 操作扣減庫存時觸發。
 * 包含工單 ID，用於追溯物料消耗的來源。
 */
public class MaterialConsumedEvent extends BaseDomainEvent {

    private final String materialId;
    private final String materialName;
    private final int quantity;
    private final String workOrderId;

    public MaterialConsumedEvent(String aggregateId, String materialId,
                                  String materialName, int quantity, String workOrderId) {
        super(aggregateId);
        this.materialId = materialId;
        this.materialName = materialName;
        this.quantity = quantity;
        this.workOrderId = workOrderId;
    }

    public String getMaterialId() {
        return materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getWorkOrderId() {
        return workOrderId;
    }

    @Override
    public String toString() {
        return "MaterialConsumedEvent{" +
                "aggregateId='" + getAggregateId() + "'" +
                ", materialId='" + materialId + "'" +
                ", materialName='" + materialName + "'" +
                ", quantity=" + quantity +
                ", workOrderId='" + workOrderId + "'" +
                ", occurredOn=" + getOccurredOn() +
                "}";
    }
}
