package com.mes.cloud.material.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

/**
 * [DDD Pattern: Domain Event - 低庫存預警]
 * [SOLID: SRP - 只描述「庫存低於最低標準」這一事實]
 *
 * 當物料消耗後庫存低於最低標準時觸發。
 * 下游消費者可據此觸發自動補貨或發送預警通知。
 */
public class LowStockAlertEvent extends BaseDomainEvent {

    private final String materialId;
    private final String materialName;
    private final int currentStock;
    private final int minimumStock;

    public LowStockAlertEvent(String aggregateId, String materialId,
                               String materialName, int currentStock, int minimumStock) {
        super(aggregateId);
        this.materialId = materialId;
        this.materialName = materialName;
        this.currentStock = currentStock;
        this.minimumStock = minimumStock;
    }

    public String getMaterialId() {
        return materialId;
    }

    public String getMaterialName() {
        return materialName;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public int getMinimumStock() {
        return minimumStock;
    }

    @Override
    public String toString() {
        return "LowStockAlertEvent{" +
                "aggregateId='" + getAggregateId() + "'" +
                ", materialId='" + materialId + "'" +
                ", materialName='" + materialName + "'" +
                ", currentStock=" + currentStock +
                ", minimumStock=" + minimumStock +
                ", occurredOn=" + getOccurredOn() +
                "}";
    }
}
