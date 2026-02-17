package com.mes.cloud.material.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.cloud.material.application.MaterialApplicationService;
import com.mes.cloud.material.application.command.ConsumeMaterialCommand;
import com.mes.cloud.material.application.command.ReceiveMaterialCommand;
import com.mes.cloud.material.application.command.RegisterMaterialCommand;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * [Hexagonal Architecture: Inbound Adapter 測試]
 * [CQRS Pattern: Command Side 控制器測試]
 *
 * 使用 @WebMvcTest 只載入 Web 層，mock MaterialApplicationService。
 */
@DisplayName("MaterialCommandController 測試")
@WebMvcTest(MaterialCommandController.class)
class MaterialCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MaterialApplicationService applicationService;

    @Nested
    @DisplayName("POST /api/v1/materials - 註冊物料")
    class RegisterMaterialTests {

        @Test
        @DisplayName("合法請求應回傳 201 Created")
        void shouldReturn201ForValidRequest() throws Exception {
            when(applicationService.registerMaterial(any())).thenReturn("MAT-001");

            RegisterMaterialCommand command = new RegisterMaterialCommand(
                    "不鏽鋼板", "RAW_MATERIAL", "KG", "公斤", 100, 20,
                    "SUP-001", "台灣鋼鐵", "02-12345678");

            mockMvc.perform(post("/api/v1/materials")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.code").value(201))
                    .andExpect(jsonPath("$.data").value("MAT-001"));
        }

        @Test
        @DisplayName("缺少必要欄位應回傳 400 Bad Request")
        void shouldReturn400ForMissingFields() throws Exception {
            String emptyJson = "{}";

            mockMvc.perform(post("/api/v1/materials")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(emptyJson))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.code").value(400));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/materials/{id}/receive - 物料入庫")
    class ReceiveMaterialTests {

        @Test
        @DisplayName("合法請求應回傳 200 OK")
        void shouldReturn200ForValidRequest() throws Exception {
            ReceiveMaterialCommand command = new ReceiveMaterialCommand("MAT-001", 50, "SUP-001");

            mockMvc.perform(put("/api/v1/materials/MAT-001/receive")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("物料入庫成功"));
        }

        @Test
        @DisplayName("物料不存在應回傳 404")
        void shouldReturn404WhenMaterialNotFound() throws Exception {
            doThrow(new EntityNotFoundException("Material", "NOT-EXIST"))
                    .when(applicationService).receiveMaterial(any());

            ReceiveMaterialCommand command = new ReceiveMaterialCommand("NOT-EXIST", 50, "SUP-001");

            mockMvc.perform(put("/api/v1/materials/NOT-EXIST/receive")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }
    }

    @Nested
    @DisplayName("PUT /api/v1/materials/{id}/consume - 物料消耗")
    class ConsumeMaterialTests {

        @Test
        @DisplayName("合法請求應回傳 200 OK")
        void shouldReturn200ForValidRequest() throws Exception {
            ConsumeMaterialCommand command = new ConsumeMaterialCommand("MAT-001", 30, "WO-001");

            mockMvc.perform(put("/api/v1/materials/MAT-001/consume")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.message").value("物料消耗成功"));
        }

        @Test
        @DisplayName("庫存不足應回傳 409 Conflict")
        void shouldReturn409WhenInsufficientStock() throws Exception {
            doThrow(new BusinessRuleViolationException("庫存不足"))
                    .when(applicationService).consumeMaterial(any());

            ConsumeMaterialCommand command = new ConsumeMaterialCommand("MAT-001", 999, "WO-001");

            mockMvc.perform(put("/api/v1/materials/MAT-001/consume")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(command)))
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("$.code").value(409));
        }
    }
}
