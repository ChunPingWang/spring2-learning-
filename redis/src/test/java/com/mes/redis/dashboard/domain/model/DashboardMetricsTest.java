package com.mes.redis.dashboard.domain.model;

import com.mes.common.ddd.event.DomainEvent;
import com.mes.redis.dashboard.domain.event.DashboardUpdatedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * DashboardMetrics 聚合根單元測試。
 * 驗證建立、更新摘要、設備狀態管理、過期判斷、領域事件等核心邏輯。
 */
@DisplayName("DashboardMetrics 聚合根測試")
class DashboardMetricsTest {

    private DashboardMetrics metrics;
    private DashboardMetricsId metricsId;
    private CacheExpiry cacheExpiry;

    @BeforeEach
    void setUp() {
        metricsId = DashboardMetricsId.of("LINE-A", "20240101");
        cacheExpiry = new CacheExpiry(1800, LocalDateTime.now());
        metrics = new DashboardMetrics(metricsId, "LINE-A", cacheExpiry);
    }

    @Nested
    @DisplayName("建立測試")
    class CreationTest {

        @Test
        @DisplayName("應可正確建立看板指標聚合根")
        void shouldCreateDashboardMetrics() {
            assertThat(metrics.getId()).isEqualTo(metricsId);
            assertThat(metrics.getLineId()).isEqualTo("LINE-A");
            assertThat(metrics.getSnapshotTime()).isNotNull();
            assertThat(metrics.getProductionSummary()).isNull();
            assertThat(metrics.getEquipmentStatuses()).isEmpty();
            assertThat(metrics.getCacheExpiry()).isEqualTo(cacheExpiry);
        }

        @Test
        @DisplayName("建立時 lineId 不可為 null")
        void shouldRejectNullLineId() {
            assertThatThrownBy(() -> new DashboardMetrics(metricsId, null, cacheExpiry))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Line ID must not be null");
        }

        @Test
        @DisplayName("建立時 cacheExpiry 不可為 null")
        void shouldRejectNullCacheExpiry() {
            assertThatThrownBy(() -> new DashboardMetrics(metricsId, "LINE-A", null))
                    .isInstanceOf(NullPointerException.class)
                    .hasMessageContaining("Cache expiry must not be null");
        }
    }

    @Nested
    @DisplayName("更新生產摘要測試")
    class UpdateProductionSummaryTest {

        @Test
        @DisplayName("應可更新生產摘要")
        void shouldUpdateProductionSummary() {
            ProductionSummary summary = new ProductionSummary(1000, 950, 50, 120.5);
            metrics.updateProductionSummary(summary);

            assertThat(metrics.getProductionSummary()).isEqualTo(summary);
            assertThat(metrics.getProductionSummary().getTotalOutput()).isEqualTo(1000);
        }

        @Test
        @DisplayName("更新摘要應註冊 DashboardUpdatedEvent")
        void shouldRegisterEventOnUpdate() {
            ProductionSummary summary = new ProductionSummary(1000, 950, 50, 120.5);
            metrics.updateProductionSummary(summary);

            List<DomainEvent> events = metrics.getDomainEvents();
            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(DashboardUpdatedEvent.class);
        }

        @Test
        @DisplayName("更新摘要時 summary 不可為 null")
        void shouldRejectNullSummary() {
            assertThatThrownBy(() -> metrics.updateProductionSummary(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("設備狀態管理測試")
    class EquipmentStatusTest {

        @Test
        @DisplayName("應可新增設備狀態快照")
        void shouldAddEquipmentStatus() {
            EquipmentStatusSnapshot snapshot = new EquipmentStatusSnapshot(
                    "EQ-001", "衝壓機A", "RUNNING", LocalDateTime.now());
            metrics.addEquipmentStatus(snapshot);

            assertThat(metrics.getEquipmentStatuses()).hasSize(1);
            assertThat(metrics.getEquipmentStatuses().get(0).getEquipmentId()).isEqualTo("EQ-001");
        }

        @Test
        @DisplayName("新增設備狀態應註冊 DashboardUpdatedEvent")
        void shouldRegisterEventOnAddEquipment() {
            EquipmentStatusSnapshot snapshot = new EquipmentStatusSnapshot(
                    "EQ-001", "衝壓機A", "RUNNING", LocalDateTime.now());
            metrics.addEquipmentStatus(snapshot);

            assertThat(metrics.getDomainEvents()).hasSize(1);
            assertThat(metrics.getDomainEvents().get(0)).isInstanceOf(DashboardUpdatedEvent.class);
        }

        @Test
        @DisplayName("應可更新已有設備的狀態")
        void shouldUpdateExistingEquipmentStatus() {
            EquipmentStatusSnapshot snapshot = new EquipmentStatusSnapshot(
                    "EQ-001", "衝壓機A", "RUNNING", LocalDateTime.now());
            metrics.addEquipmentStatus(snapshot);
            metrics.clearEvents();

            metrics.updateEquipmentStatus("EQ-001", "BREAKDOWN");

            assertThat(metrics.getEquipmentStatuses()).hasSize(1);
            assertThat(metrics.getEquipmentStatuses().get(0).getStatus()).isEqualTo("BREAKDOWN");
        }

        @Test
        @DisplayName("更新不存在的設備不應改變任何狀態")
        void shouldNotChangeAnythingForNonExistentEquipment() {
            metrics.updateEquipmentStatus("NOT-EXIST", "RUNNING");

            assertThat(metrics.getEquipmentStatuses()).isEmpty();
            assertThat(metrics.getDomainEvents()).isEmpty();
        }
    }

    @Nested
    @DisplayName("過期判斷測試")
    class ExpiryTest {

        @Test
        @DisplayName("未過期的快取應回傳 false")
        void shouldNotBeExpiredWhenTtlNotExceeded() {
            assertThat(metrics.isExpired()).isFalse();
        }

        @Test
        @DisplayName("已過期的快取應回傳 true")
        void shouldBeExpiredWhenTtlExceeded() {
            CacheExpiry expiredExpiry = new CacheExpiry(1, LocalDateTime.now().minusSeconds(5));
            DashboardMetrics expiredMetrics = new DashboardMetrics(
                    metricsId, "LINE-A", expiredExpiry);

            assertThat(expiredMetrics.isExpired()).isTrue();
        }
    }

    @Nested
    @DisplayName("領域事件測試")
    class DomainEventTest {

        @Test
        @DisplayName("清除事件後應為空")
        void shouldClearEvents() {
            ProductionSummary summary = new ProductionSummary(1000, 950, 50, 120.5);
            metrics.updateProductionSummary(summary);
            assertThat(metrics.getDomainEvents()).isNotEmpty();

            metrics.clearEvents();
            assertThat(metrics.getDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("DashboardUpdatedEvent 應包含正確的 lineId")
        void shouldContainCorrectLineIdInEvent() {
            ProductionSummary summary = new ProductionSummary(1000, 950, 50, 120.5);
            metrics.updateProductionSummary(summary);

            DashboardUpdatedEvent event = (DashboardUpdatedEvent) metrics.getDomainEvents().get(0);
            assertThat(event.getLineId()).isEqualTo("LINE-A");
            assertThat(event.getSnapshotTime()).isNotNull();
            assertThat(event.getAggregateId()).isEqualTo(metricsId.getValue());
        }
    }
}
