package com.mes.cloud.material.application.command.handler;

import com.mes.common.exception.EntityNotFoundException;
import com.mes.cloud.material.application.command.ReceiveMaterialCommand;
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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * [CQRS Pattern: Command Handler 單元測試]
 *
 * 測試 ReceiveMaterialCommandHandler 的入庫操作處理。
 */
@DisplayName("ReceiveMaterialCommandHandler 測試")
@ExtendWith(MockitoExtension.class)
class ReceiveMaterialCommandHandlerTest {

    @Mock
    private MaterialRepository repository;

    private ReceiveMaterialCommandHandler handler;

    @BeforeEach
    void setUp() {
        handler = new ReceiveMaterialCommandHandler(repository);
    }

    private Material createTestMaterial() {
        return new Material(
                MaterialId.of("MAT-001"),
                "不鏽鋼板",
                MaterialType.RAW_MATERIAL,
                MaterialUnit.KG,
                new StockLevel(100, "KG"),
                20,
                new Supplier("SUP-001", "台灣鋼鐵", "info")
        );
    }

    @Test
    @DisplayName("入庫成功後應呼叫 repository.save")
    void shouldCallSaveAfterReceive() {
        Material material = createTestMaterial();
        when(repository.findById(MaterialId.of("MAT-001"))).thenReturn(Optional.of(material));

        ReceiveMaterialCommand command = new ReceiveMaterialCommand("MAT-001", 50, "SUP-001");
        handler.handle(command);

        assertThat(material.getStockLevel().getCurrentQuantity()).isEqualTo(150);
        verify(repository).save(material);
    }

    @Test
    @DisplayName("物料不存在時應拋出 EntityNotFoundException")
    void shouldThrowWhenMaterialNotFound() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        ReceiveMaterialCommand command = new ReceiveMaterialCommand("NOT-EXIST", 50, "SUP-001");

        assertThatThrownBy(() -> handler.handle(command))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("NOT-EXIST");
    }

    @Test
    @DisplayName("入庫後物料應有 MaterialReceivedEvent")
    void shouldHaveReceivedEventAfterReceive() {
        Material material = createTestMaterial();
        when(repository.findById(MaterialId.of("MAT-001"))).thenReturn(Optional.of(material));

        ReceiveMaterialCommand command = new ReceiveMaterialCommand("MAT-001", 50, "SUP-001");
        handler.handle(command);

        assertThat(material.getDomainEvents()).hasSize(1);
    }
}
