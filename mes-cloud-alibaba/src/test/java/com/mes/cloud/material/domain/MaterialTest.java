package com.mes.cloud.material.domain;

import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.DomainException;
import com.mes.cloud.material.domain.event.LowStockAlertEvent;
import com.mes.cloud.material.domain.event.MaterialConsumedEvent;
import com.mes.cloud.material.domain.event.MaterialReceivedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [DDD Pattern: Aggregate Root 單元測試]
 *
 * 測試 Material 聚合根的：
 * 1. 建構與初始狀態
 * 2. 入庫操作與事件
 * 3. 消耗操作與事件（含低庫存預警）
 * 4. 庫存調整
 * 5. 供應商更新
 */
@DisplayName("Material 聚合根測試")
class MaterialTest {

    private Material material;

    @BeforeEach
    void setUp() {
        MaterialId id = MaterialId.of("MAT-001");
        MaterialUnit unit = MaterialUnit.KG;
        StockLevel stockLevel = new StockLevel(100, "KG");
        Supplier supplier = new Supplier("SUP-001", "台灣鋼鐵", "02-12345678");

        material = new Material(id, "不鏽鋼板", MaterialType.RAW_MATERIAL,
                unit, stockLevel, 20, supplier);
    }

    @Nested
    @DisplayName("建構與初始狀態測試")
    class ConstructionTests {

        @Test
        @DisplayName("新建的物料應有正確的初始屬性")
        void shouldHaveCorrectInitialProperties() {
            assertThat(material.getId().getValue()).isEqualTo("MAT-001");
            assertThat(material.getName()).isEqualTo("不鏽鋼板");
            assertThat(material.getMaterialType()).isEqualTo(MaterialType.RAW_MATERIAL);
            assertThat(material.getStockLevel().getCurrentQuantity()).isEqualTo(100);
            assertThat(material.getMinimumStock()).isEqualTo(20);
            assertThat(material.getSupplier().getSupplierName()).isEqualTo("台灣鋼鐵");
        }

        @Test
        @DisplayName("新建的物料不應有領域事件")
        void shouldHaveNoDomainEvents() {
            assertThat(material.getDomainEvents()).isEmpty();
        }

        @Test
        @DisplayName("物料名稱不可為空")
        void shouldRejectNullName() {
            assertThatThrownBy(() -> new Material(
                    MaterialId.of("MAT-002"), null, MaterialType.RAW_MATERIAL,
                    MaterialUnit.KG, new StockLevel(0, "KG"), 10,
                    new Supplier("SUP-001", "供應商", "info")))
                    .isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("物料名稱不可為空白字串")
        void shouldRejectBlankName() {
            assertThatThrownBy(() -> new Material(
                    MaterialId.of("MAT-002"), "  ", MaterialType.RAW_MATERIAL,
                    MaterialUnit.KG, new StockLevel(0, "KG"), 10,
                    new Supplier("SUP-001", "供應商", "info")))
                    .isInstanceOf(DomainException.class);
        }

        @Test
        @DisplayName("最低庫存不可為負數")
        void shouldRejectNegativeMinimumStock() {
            assertThatThrownBy(() -> new Material(
                    MaterialId.of("MAT-002"), "物料", MaterialType.RAW_MATERIAL,
                    MaterialUnit.KG, new StockLevel(0, "KG"), -1,
                    new Supplier("SUP-001", "供應商", "info")))
                    .isInstanceOf(DomainException.class);
        }
    }

    @Nested
    @DisplayName("入庫操作 (receive) 測試")
    class ReceiveTests {

        @Test
        @DisplayName("入庫後庫存應增加")
        void shouldIncreaseStock() {
            material.receive(50);
            assertThat(material.getStockLevel().getCurrentQuantity()).isEqualTo(150);
        }

        @Test
        @DisplayName("入庫後應註冊 MaterialReceivedEvent")
        void shouldRegisterReceivedEvent() {
            material.receive(50);
            List<DomainEvent> events = material.getDomainEvents();

            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(MaterialReceivedEvent.class);

            MaterialReceivedEvent event = (MaterialReceivedEvent) events.get(0);
            assertThat(event.getMaterialId()).isEqualTo("MAT-001");
            assertThat(event.getMaterialName()).isEqualTo("不鏽鋼板");
            assertThat(event.getQuantity()).isEqualTo(50);
            assertThat(event.getSupplierId()).isEqualTo("SUP-001");
        }

        @Test
        @DisplayName("連續入庫應累積庫存")
        void shouldAccumulateStock() {
            material.receive(30);
            material.receive(20);
            assertThat(material.getStockLevel().getCurrentQuantity()).isEqualTo(150);
            assertThat(material.getDomainEvents()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("消耗操作 (consume) 測試")
    class ConsumeTests {

        @Test
        @DisplayName("消耗後庫存應減少")
        void shouldDecreaseStock() {
            material.consume(30, "WO-001");
            assertThat(material.getStockLevel().getCurrentQuantity()).isEqualTo(70);
        }

        @Test
        @DisplayName("消耗後應註冊 MaterialConsumedEvent")
        void shouldRegisterConsumedEvent() {
            material.consume(30, "WO-001");
            List<DomainEvent> events = material.getDomainEvents();

            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(MaterialConsumedEvent.class);

            MaterialConsumedEvent event = (MaterialConsumedEvent) events.get(0);
            assertThat(event.getMaterialId()).isEqualTo("MAT-001");
            assertThat(event.getQuantity()).isEqualTo(30);
            assertThat(event.getWorkOrderId()).isEqualTo("WO-001");
        }

        @Test
        @DisplayName("庫存不足時消耗應拋出 BusinessRuleViolationException")
        void shouldThrowWhenInsufficientStock() {
            assertThatThrownBy(() -> material.consume(200, "WO-001"))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("庫存不足");
        }

        @Test
        @DisplayName("消耗後低於最低庫存應註冊 LowStockAlertEvent")
        void shouldRegisterLowStockAlertWhenBelowMinimum() {
            // 初始庫存 100，最低庫存 20，消耗 85 後剩 15 < 20
            material.consume(85, "WO-001");
            List<DomainEvent> events = material.getDomainEvents();

            assertThat(events).hasSize(2);
            assertThat(events.get(0)).isInstanceOf(MaterialConsumedEvent.class);
            assertThat(events.get(1)).isInstanceOf(LowStockAlertEvent.class);

            LowStockAlertEvent alertEvent = (LowStockAlertEvent) events.get(1);
            assertThat(alertEvent.getCurrentStock()).isEqualTo(15);
            assertThat(alertEvent.getMinimumStock()).isEqualTo(20);
        }

        @Test
        @DisplayName("消耗後庫存仍充足時不應註冊 LowStockAlertEvent")
        void shouldNotRegisterLowStockAlertWhenSufficient() {
            material.consume(30, "WO-001");
            List<DomainEvent> events = material.getDomainEvents();

            assertThat(events).hasSize(1);
            assertThat(events.get(0)).isInstanceOf(MaterialConsumedEvent.class);
        }
    }

    @Nested
    @DisplayName("庫存判斷 (isLowStock) 測試")
    class LowStockTests {

        @Test
        @DisplayName("庫存充足時 isLowStock 應為 false")
        void shouldReturnFalseWhenStockSufficient() {
            assertThat(material.isLowStock()).isFalse();
        }

        @Test
        @DisplayName("庫存低於最低標準時 isLowStock 應為 true")
        void shouldReturnTrueWhenStockBelowMinimum() {
            material.consume(85, "WO-001");
            assertThat(material.isLowStock()).isTrue();
        }
    }

    @Nested
    @DisplayName("庫存調整 (adjustStock) 測試")
    class AdjustStockTests {

        @Test
        @DisplayName("調整後庫存應更新為新值")
        void shouldUpdateToNewLevel() {
            material.adjustStock(50);
            assertThat(material.getStockLevel().getCurrentQuantity()).isEqualTo(50);
        }

        @Test
        @DisplayName("調整為零應成功")
        void shouldAllowZeroStock() {
            material.adjustStock(0);
            assertThat(material.getStockLevel().getCurrentQuantity()).isEqualTo(0);
        }
    }

    @Nested
    @DisplayName("更新供應商 (updateSupplier) 測試")
    class UpdateSupplierTests {

        @Test
        @DisplayName("更新供應商後資訊應更新")
        void shouldUpdateSupplier() {
            Supplier newSupplier = new Supplier("SUP-002", "新供應商", "03-87654321");
            material.updateSupplier(newSupplier);

            assertThat(material.getSupplier().getSupplierId()).isEqualTo("SUP-002");
            assertThat(material.getSupplier().getSupplierName()).isEqualTo("新供應商");
        }

        @Test
        @DisplayName("供應商不可為 null")
        void shouldRejectNullSupplier() {
            assertThatThrownBy(() -> material.updateSupplier(null))
                    .isInstanceOf(NullPointerException.class);
        }
    }
}
