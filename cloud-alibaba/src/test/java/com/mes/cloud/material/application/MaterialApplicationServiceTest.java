package com.mes.cloud.material.application;

import com.mes.common.cqrs.CommandBus;
import com.mes.common.cqrs.QueryBus;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.cloud.material.application.command.ConsumeMaterialCommand;
import com.mes.cloud.material.application.command.RegisterMaterialCommand;
import com.mes.cloud.material.application.query.dto.MaterialView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * [DDD Pattern: Application Service 單元測試]
 * [Spring Cloud Alibaba: Sentinel 降級方法測試]
 *
 * 測試 MaterialApplicationService 的服務方法與降級方法。
 */
@DisplayName("MaterialApplicationService 應用服務測試")
@ExtendWith(MockitoExtension.class)
class MaterialApplicationServiceTest {

    @Mock
    private CommandBus commandBus;

    @Mock
    private QueryBus queryBus;

    private MaterialApplicationService service;

    @BeforeEach
    void setUp() {
        service = new MaterialApplicationService(commandBus, queryBus);
    }

    @Nested
    @DisplayName("正常業務方法測試")
    class NormalMethodTests {

        @Test
        @DisplayName("registerMaterial 應派送命令到 CommandBus")
        void shouldDispatchRegisterCommand() {
            when(commandBus.<RegisterMaterialCommand, String>dispatch(any())).thenReturn("MAT-001");

            RegisterMaterialCommand command = new RegisterMaterialCommand(
                    "物料", "RAW_MATERIAL", "KG", "公斤", 100, 20,
                    "SUP-001", "供應商", "info");
            String result = service.registerMaterial(command);

            assertThat(result).isEqualTo("MAT-001");
            verify(commandBus).dispatch(command);
        }

        @Test
        @DisplayName("getMaterial 應派送查詢到 QueryBus")
        void shouldDispatchGetMaterialQuery() {
            MaterialView expectedView = new MaterialView();
            expectedView.setId("MAT-001");
            expectedView.setName("不鏽鋼板");
            when(queryBus.dispatch(any())).thenReturn(expectedView);

            MaterialView result = service.getMaterial("MAT-001");

            assertThat(result.getId()).isEqualTo("MAT-001");
            assertThat(result.getName()).isEqualTo("不鏽鋼板");
        }
    }

    @Nested
    @DisplayName("Sentinel 降級方法測試")
    class FallbackMethodTests {

        @Test
        @DisplayName("getMaterialFallback 應回傳降級的 MaterialView")
        void shouldReturnFallbackView() {
            MaterialView result = service.getMaterialFallback("MAT-001",
                    new EntityNotFoundException("Material", "MAT-001"));

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo("MAT-001");
            assertThat(result.getName()).isEqualTo("資料暫時無法取得");
        }

        @Test
        @DisplayName("consumeMaterial 應派送命令到 CommandBus")
        void shouldDispatchConsumeCommand() {
            ConsumeMaterialCommand command = new ConsumeMaterialCommand("MAT-001", 30, "WO-001");
            service.consumeMaterial(command);

            verify(commandBus).dispatch(command);
        }
    }
}
