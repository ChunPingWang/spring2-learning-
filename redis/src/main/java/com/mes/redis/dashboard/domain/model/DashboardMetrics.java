package com.mes.redis.dashboard.domain.model;

import com.mes.common.ddd.annotation.AggregateRoot;
import com.mes.common.ddd.model.BaseAggregateRoot;
import com.mes.redis.dashboard.domain.event.DashboardUpdatedEvent;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * [DDD Pattern: Aggregate Root - 生產看板指標]
 * [SOLID: SRP - 負責維護看板指標聚合的一致性]
 * [SOLID: OCP - 可透過新增方法擴展行為，不修改現有邏輯]
 *
 * 生產看板指標聚合根，封裝某條產線在某一天的即時數據：
 * <ul>
 *   <li>產線 ID 與快照時間</li>
 *   <li>生產摘要（ProductionSummary）</li>
 *   <li>設備狀態快照列表（EquipmentStatusSnapshot）</li>
 *   <li>快取過期設定（CacheExpiry）</li>
 * </ul>
 *
 * 所有狀態變更都透過此聚合根的方法進行，
 * 並在適當時機註冊 {@link DashboardUpdatedEvent}。
 */
@AggregateRoot
public class DashboardMetrics extends BaseAggregateRoot<DashboardMetricsId> {

    private final String lineId;
    private LocalDateTime snapshotTime;
    private ProductionSummary productionSummary;
    private final List<EquipmentStatusSnapshot> equipmentStatuses;
    private CacheExpiry cacheExpiry;

    public DashboardMetrics(DashboardMetricsId id, String lineId, CacheExpiry cacheExpiry) {
        super(id);
        this.lineId = Objects.requireNonNull(lineId, "Line ID must not be null");
        this.snapshotTime = LocalDateTime.now();
        this.equipmentStatuses = new ArrayList<>();
        this.cacheExpiry = Objects.requireNonNull(cacheExpiry, "Cache expiry must not be null");
    }

    /**
     * 更新生產摘要。
     * 更新後會註冊 {@link DashboardUpdatedEvent}。
     *
     * @param summary 新的生產摘要
     */
    public void updateProductionSummary(ProductionSummary summary) {
        this.productionSummary = Objects.requireNonNull(summary, "Production summary must not be null");
        this.snapshotTime = LocalDateTime.now();
        touch();
        registerEvent(new DashboardUpdatedEvent(getId().getValue(), lineId, snapshotTime));
    }

    /**
     * 新增設備狀態快照。
     *
     * @param snapshot 設備狀態快照
     */
    public void addEquipmentStatus(EquipmentStatusSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "Equipment status snapshot must not be null");
        this.equipmentStatuses.add(snapshot);
        this.snapshotTime = LocalDateTime.now();
        touch();
        registerEvent(new DashboardUpdatedEvent(getId().getValue(), lineId, snapshotTime));
    }

    /**
     * 更新指定設備的狀態。
     * 若設備不存在則不做任何事。
     *
     * @param equipmentId 設備 ID
     * @param newStatus   新狀態
     */
    public void updateEquipmentStatus(String equipmentId, String newStatus) {
        for (int i = 0; i < equipmentStatuses.size(); i++) {
            EquipmentStatusSnapshot existing = equipmentStatuses.get(i);
            if (existing.getEquipmentId().equals(equipmentId)) {
                EquipmentStatusSnapshot updated = new EquipmentStatusSnapshot(
                        existing.getEquipmentId(),
                        existing.getEquipmentName(),
                        newStatus,
                        LocalDateTime.now());
                equipmentStatuses.set(i, updated);
                this.snapshotTime = LocalDateTime.now();
                touch();
                registerEvent(new DashboardUpdatedEvent(getId().getValue(), lineId, snapshotTime));
                return;
            }
        }
    }

    /**
     * 判斷此看板指標是否已過期。
     *
     * @return true 表示已過期
     */
    public boolean isExpired() {
        return cacheExpiry.isExpired();
    }

    public String getLineId() {
        return lineId;
    }

    public LocalDateTime getSnapshotTime() {
        return snapshotTime;
    }

    public ProductionSummary getProductionSummary() {
        return productionSummary;
    }

    public List<EquipmentStatusSnapshot> getEquipmentStatuses() {
        return Collections.unmodifiableList(equipmentStatuses);
    }

    public CacheExpiry getCacheExpiry() {
        return cacheExpiry;
    }
}
