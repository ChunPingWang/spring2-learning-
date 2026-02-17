package com.mes.cloud.material.application.command.handler;

import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.ddd.event.DomainEventPublisher;
import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.cloud.material.application.command.ConsumeMaterialCommand;
import com.mes.cloud.material.domain.Material;
import com.mes.cloud.material.domain.MaterialId;
import com.mes.cloud.material.domain.MaterialType;
import com.mes.cloud.material.domain.MaterialUnit;
import com.mes.cloud.material.domain.StockLevel;
import com.mes.cloud.material.domain.Supplier;
import com.mes.cloud.material.domain.repository.MaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * [CQRS Pattern: Command Handler 單元測試]
 *
 * 測試 ConsumeMaterialCommandHandler 的消耗操作處理，
 * 包含事件發佈與低庫存預警。
 */
@DisplayName("ConsumeMaterialCommandHandler 測試")
@ExtendWith(MockitoExtension.class)
class ConsumeMaterialCommandHandlerTest {

    @Mock
    private MaterialRepository repository;

    @Mock
    private DomainEventPublisher eventPublisher;

    @Captor
    private ArgumentCaptor<DomainEvent> eventCaptor;

    private ConsumeMaterialCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ConsumeMaterialCommandHandler(repository, eventPublisher);
    }

    private Material createTestMaterial(int stock, int minimumStock) {
        return new Material(
                MaterialId.of("MAT-001"),
                "不鏽鋼板",
                MaterialType.RAW_MATERIAL,
                MaterialUnit.KG,
                new StockLevel(stock, "KG"),
                minimumStock,
                new Supplier("SUP-001", "台灣鋼鐵", "info")
        );
    }

    @Test
    @DisplayName("消耗成功後應發佈 MaterialConsumedEvent")
    void shouldPublishConsumedEvent() {
        Material material = createTestMaterial(100, 10);
        when(repository.findById(MaterialId.of("MAT-001"))).thenReturn(Optional.of(material));

        ConsumeMaterialCommand command = new ConsumeMaterialCommand("MAT-001", 30, "WO-001");
        handler.handle(command);

        verify(repository).save(material);
        verify(eventPublisher, times(1)).publish(eventCaptor.capture());
    }

    @Test
    @DisplayName("物料不存在時應拋出 EntityNotFoundException")
    void shouldThrowWhenMaterialNotFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        ConsumeMaterialCommand command = new ConsumeMaterialCommand("NOT-EXIST", 30, "WO-001");

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(EntityNotFoundException.class);
    }

    @Test
    @DisplayName("庫存不足時應拋出 BusinessRuleViolationException")
    void shouldThrowWhenInsufficientStock() {
        Material material = createTestMaterial(10, 5);
        when(repository.findById(MaterialId.of("MAT-001"))).thenReturn(Optional.of(material));

        ConsumeMaterialCommand command = new ConsumeMaterialCommand("MAT-001", 50, "WO-001");

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(BusinessRuleViolationException.class);
    }

    @Test
    @DisplayName("消耗後低於最低庫存應發佈兩個事件（ConsumedEvent + LowStockAlertEvent）")
    void shouldPublishLowStockAlertWhenBelowMinimum() {
        Material material = createTestMaterial(30, 20);
        when(repository.findById(MaterialId.of("MAT-001"))).thenReturn(Optional.of(material));

        // 消耗 25 後剩 5，低於最低庫存 20
        ConsumeMaterialCommand command = new ConsumeMaterialCommand("MAT-001", 25, "WO-001");
        handler.handle(command);

        verify(eventPublisher, times(2)).publish(eventCaptor.capture());
        assertThat(eventCaptor.getAllValues()).hasSize(2);
    }
}
