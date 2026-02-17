package com.mes.redis.dashboard.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * EquipmentStatusSnapshot 值物件單元測試。
 * 驗證建立、getter、相等性。
 */
@DisplayName("EquipmentStatusSnapshot 值物件測試")
class EquipmentStatusSnapshotTest {

    @Test
    @DisplayName("應可正確建立設備狀態快照")
    void shouldCreateEquipmentStatusSnapshot() {
        LocalDateTime now = LocalDateTime.now();
        EquipmentStatusSnapshot snapshot = new EquipmentStatusSnapshot(
                "EQ-001", "衝壓機A", "RUNNING", now);

        assertThat(snapshot.getEquipmentId()).isEqualTo("EQ-001");
        assertThat(snapshot.getEquipmentName()).isEqualTo("衝壓機A");
        assertThat(snapshot.getStatus()).isEqualTo("RUNNING");
        assertThat(snapshot.getLastUpdated()).isEqualTo(now);
    }

    @Test
    @DisplayName("設備 ID 不可為 null")
    void shouldRejectNullEquipmentId() {
        assertThatThrownBy(() -> new EquipmentStatusSnapshot(
                null, "衝壓機A", "RUNNING", LocalDateTime.now()))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Equipment ID must not be null");
    }

    @Test
    @DisplayName("狀態不可為 null")
    void shouldRejectNullStatus() {
        assertThatThrownBy(() -> new EquipmentStatusSnapshot(
                "EQ-001", "衝壓機A", null, LocalDateTime.now()))
                .isInstanceOf(NullPointerException.class)
                .hasMessageContaining("Status must not be null");
    }

    @Test
    @DisplayName("相同屬性的快照應相等")
    void shouldBeEqualWithSameValues() {
        LocalDateTime now = LocalDateTime.now();
        EquipmentStatusSnapshot s1 = new EquipmentStatusSnapshot("EQ-001", "衝壓機A", "RUNNING", now);
        EquipmentStatusSnapshot s2 = new EquipmentStatusSnapshot("EQ-001", "衝壓機A", "RUNNING", now);

        assertThat(s1).isEqualTo(s2);
        assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
    }

    @Test
    @DisplayName("不同狀態的快照應不相等")
    void shouldNotBeEqualWithDifferentStatus() {
        LocalDateTime now = LocalDateTime.now();
        EquipmentStatusSnapshot s1 = new EquipmentStatusSnapshot("EQ-001", "衝壓機A", "RUNNING", now);
        EquipmentStatusSnapshot s2 = new EquipmentStatusSnapshot("EQ-001", "衝壓機A", "IDLE", now);

        assertThat(s1).isNotEqualTo(s2);
    }
}
