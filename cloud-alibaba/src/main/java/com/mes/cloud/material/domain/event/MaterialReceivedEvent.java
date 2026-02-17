package com.mes.cloud.material.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event - 物料已入庫]
 * [SOLID: SRP - 只描述「物料已入庫」這一事實]
 *
 * 當物料透過 receive() 操作增加庫存時觸發。
 * 下游消費者可據此更新庫存報表或通知相關人員。
 */
public class MaterialReceivedEvent extends BaseDomainEvent {

    private final String materialId;
    private final String materialName;
    private final int quantity;
    private final String supplierId;

    public MaterialReceivedEvent(String aggregateId, String materialId,
                                  String materialName, int quantity, String supplierId) {
        super(aggregateId);
        this.materialId = materialId;
        this.materialName = materialName;
        this.quantity = quantity;
        this.supplierId = supplierId;
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

    public String getSupplierId() {
        return supplierId;
    }

    @Override
    public String toString() {
        return "MaterialReceivedEvent{" +
                "aggregateId='" + getAggregateId() + "'" +
                ", materialId='" + materialId + "'" +
                ", materialName='" + materialName + "'" +
                ", quantity=" + quantity +
                ", supplierId='" + supplierId + "'" +
                ", occurredOn=" + getOccurredOn() +
                "}";
    }
}
