package com.mes.cloud.material.domain.service;

import com.mes.cloud.material.domain.Material;
import com.mes.cloud.material.domain.MaterialId;
import com.mes.cloud.material.domain.MaterialType;
import com.mes.cloud.material.domain.MaterialUnit;
import com.mes.cloud.material.domain.StockLevel;
import com.mes.cloud.material.domain.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * [DDD Pattern: Domain Service 單元測試]
 *
 * 測試 StockDomainService 的庫存可用性檢查與補貨計算。
 */
@DisplayName("StockDomainService 領域服務測試")
class StockDomainServiceTest {

    private StockDomainService service;

    @BeforeEach
    void setUp() {
        service = new StockDomainService();
    }

    private Material createMaterial(int currentStock, int minimumStock) {
        return new Material(
                MaterialId.of("MAT-001"),
                "測試物料",
                MaterialType.RAW_MATERIAL,
                MaterialUnit.KG,
                new StockLevel(currentStock, "KG"),
                minimumStock,
                new Supplier("SUP-001", "供應商", "info")
        );
    }

    @Nested
    @DisplayName("庫存可用性檢查 (checkStockAvailability)")
    class CheckStockAvailabilityTests {

        @Test
        @DisplayName("庫存充足時應回傳 true")
        void shouldReturnTrueWhenStockSufficient() {
            Material material = createMaterial(100, 20);
            assertThat(service.checkStockAvailability(material, 50)).isTrue();
        }

        @Test
        @DisplayName("庫存等於需求時應回傳 true")
        void shouldReturnTrueWhenStockEqualsRequired() {
            Material material = createMaterial(100, 20);
            assertThat(service.checkStockAvailability(material, 100)).isTrue();
        }

        @Test
        @DisplayName("庫存不足時應回傳 false")
        void shouldReturnFalseWhenStockInsufficient() {
            Material material = createMaterial(50, 20);
            assertThat(service.checkStockAvailability(material, 100)).isFalse();
        }
    }

    @Nested
    @DisplayName("補貨數量計算 (calculateReorderQuantity)")
    class CalculateReorderQuantityTests {

        @Test
        @DisplayName("庫存低於 2 倍最低庫存時應計算出正確的補貨數量")
        void shouldCalculateCorrectReorderQuantity() {
            // minimumStock=20, targetLevel=40, currentStock=10, reorder=30
            Material material = createMaterial(10, 20);
            assertThat(service.calculateReorderQuantity(material)).isEqualTo(30);
        }

        @Test
        @DisplayName("庫存已高於 2 倍最低庫存時應回傳 0")
        void shouldReturnZeroWhenStockAboveTarget() {
            // minimumStock=20, targetLevel=40, currentStock=100, reorder=max(40-100,0)=0
            Material material = createMaterial(100, 20);
            assertThat(service.calculateReorderQuantity(material)).isEqualTo(0);
        }
    }
}
