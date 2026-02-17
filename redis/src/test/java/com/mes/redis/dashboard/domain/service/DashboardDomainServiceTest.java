package com.mes.redis.dashboard.domain.service;

import com.mes.redis.dashboard.domain.model.EquipmentStatusSnapshot;
import com.mes.redis.dashboard.domain.model.ProductionSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DashboardDomainService 領域服務單元測試。
 * 驗證產線效率計算與產線健康判斷邏輯。
 */
@DisplayName("DashboardDomainService 領域服務測試")
class DashboardDomainServiceTest {

    private DashboardDomainService service;

    @BeforeEach
    void setUp() {
        service = new DashboardDomainService();
    }

    @Nested
    @DisplayName("計算產線效率測試")
    class CalculateLineEfficiencyTest {

        @Test
        @DisplayName("無設備時效率應等於良率")
        void shouldReturnYieldRateWhenNoEquipment() {
            ProductionSummary summary = new ProductionSummary(1000, 950, 50, 120.0);
            List<EquipmentStatusSnapshot> statuses = Collections.emptyList();

            double efficiency = service.calculateLineEfficiency(summary, statuses);

            assertThat(efficiency).isEqualTo(0.95);
        }

        @Test
        @DisplayName("全部設備運行時效率應等於良率")
        void shouldEqualYieldRateWhenAllRunning() {
            ProductionSummary summary = new ProductionSummary(1000, 950, 50, 120.0);
            List<EquipmentStatusSnapshot> statuses = Arrays.asList(
                    new EquipmentStatusSnapshot("EQ-001", "A", "RUNNING", LocalDateTime.now()),
                    new EquipmentStatusSnapshot("EQ-002", "B", "RUNNING", LocalDateTime.now()));

            double efficiency = service.calculateLineEfficiency(summary, statuses);

            assertThat(efficiency).isEqualTo(0.95);
        }

        @Test
        @DisplayName("部分設備閒置時效率應降低")
        void shouldReduceEfficiencyWhenSomeIdle() {
            ProductionSummary summary = new ProductionSummary(1000, 950, 50, 120.0);
            List<EquipmentStatusSnapshot> statuses = Arrays.asList(
                    new EquipmentStatusSnapshot("EQ-001", "A", "RUNNING", LocalDateTime.now()),
                    new EquipmentStatusSnapshot("EQ-002", "B", "IDLE", LocalDateTime.now()));

            double efficiency = service.calculateLineEfficiency(summary, statuses);

            // 良率 0.95 * 運行比例 0.5 = 0.475
            assertThat(efficiency).isEqualTo(0.475);
        }

        @Test
        @DisplayName("statuses 為 null 時效率應等於良率")
        void shouldReturnYieldRateWhenStatusesNull() {
            ProductionSummary summary = new ProductionSummary(1000, 950, 50, 120.0);

            double efficiency = service.calculateLineEfficiency(summary, null);

            assertThat(efficiency).isEqualTo(0.95);
        }
    }

    @Nested
    @DisplayName("產線健康判斷測試")
    class IsLineHealthyTest {

        @Test
        @DisplayName("無故障設備時產線應為健康")
        void shouldBeHealthyWhenNoBreakdown() {
            List<EquipmentStatusSnapshot> statuses = Arrays.asList(
                    new EquipmentStatusSnapshot("EQ-001", "A", "RUNNING", LocalDateTime.now()),
                    new EquipmentStatusSnapshot("EQ-002", "B", "IDLE", LocalDateTime.now()));

            assertThat(service.isLineHealthy(statuses)).isTrue();
        }

        @Test
        @DisplayName("有故障設備時產線應不健康")
        void shouldNotBeHealthyWhenBreakdownExists() {
            List<EquipmentStatusSnapshot> statuses = Arrays.asList(
                    new EquipmentStatusSnapshot("EQ-001", "A", "RUNNING", LocalDateTime.now()),
                    new EquipmentStatusSnapshot("EQ-002", "B", "BREAKDOWN", LocalDateTime.now()));

            assertThat(service.isLineHealthy(statuses)).isFalse();
        }
    }
}
