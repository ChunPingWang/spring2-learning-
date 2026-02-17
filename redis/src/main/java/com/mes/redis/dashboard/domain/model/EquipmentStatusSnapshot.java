package com.mes.redis.dashboard.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * [DDD Pattern: Value Object - 設備狀態快照]
 * [SOLID: SRP - 只負責記錄設備在某一時刻的狀態]
 *
 * 不可變的設備狀態快照值物件。
 * 記錄設備 ID、名稱、當前狀態（RUNNING/IDLE/BREAKDOWN）及最後更新時間。
 */
@ValueObject
public class EquipmentStatusSnapshot extends BaseValueObject {

    private final String equipmentId;
    private final String equipmentName;
    private final String status;
    private final LocalDateTime lastUpdated;

    public EquipmentStatusSnapshot(String equipmentId, String equipmentName,
                                   String status, LocalDateTime lastUpdated) {
        this.equipmentId = Objects.requireNonNull(equipmentId, "Equipment ID must not be null");
        this.equipmentName = Objects.requireNonNull(equipmentName, "Equipment name must not be null");
        this.status = Objects.requireNonNull(status, "Status must not be null");
        this.lastUpdated = Objects.requireNonNull(lastUpdated, "Last updated must not be null");
    }

    public String getEquipmentId() {
        return equipmentId;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public String getStatus() {
        return status;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.asList(equipmentId, equipmentName, status, lastUpdated);
    }
}
