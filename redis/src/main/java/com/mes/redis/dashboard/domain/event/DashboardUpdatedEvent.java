package com.mes.redis.dashboard.domain.event;

import com.mes.common.ddd.event.BaseDomainEvent;

import java.time.LocalDateTime;

/**
 * [DDD Pattern: Domain Event - 看板已更新事件]
 * [SOLID: SRP - 只負責攜帶看板更新的事實資訊]
 *
 * 當看板指標的生產摘要或設備狀態被更新時觸發。
 * 不可變：事件一旦建立就不能修改。
 */
public class DashboardUpdatedEvent extends BaseDomainEvent {

    private final String lineId;
    private final LocalDateTime snapshotTime;

    public DashboardUpdatedEvent(String aggregateId, String lineId, LocalDateTime snapshotTime) {
        super(aggregateId);
        this.lineId = lineId;
        this.snapshotTime = snapshotTime;
    }

    public String getLineId() {
        return lineId;
    }

    public LocalDateTime getSnapshotTime() {
        return snapshotTime;
    }

    @Override
    public String toString() {
        return "DashboardUpdatedEvent{" +
                "eventId='" + getEventId() + '\'' +
                ", aggregateId='" + getAggregateId() + '\'' +
                ", lineId='" + lineId + '\'' +
                ", snapshotTime=" + snapshotTime +
                '}';
    }
}
