package com.mes.cloud.material.adapter.in.web;

import com.mes.common.exception.EntityNotFoundException;
import com.mes.cloud.material.application.MaterialApplicationService;
import com.mes.cloud.material.application.query.dto.MaterialView;
import com.mes.cloud.material.application.query.dto.StockAlertView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * [Hexagonal Architecture: Inbound Adapter 測試]
 * [CQRS Pattern: Query Side 控制器測試]
 *
 * 使用 @WebMvcTest 只載入 Web 層，mock MaterialApplicationService。
 */
@DisplayName("MaterialQueryController 測試")
@WebMvcTest(MaterialQueryController.class)
class MaterialQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MaterialApplicationService applicationService;

    @Nested
    @DisplayName("GET /api/v1/materials/{id} - 查詢單筆物料")
    class GetMaterialTests {

        @Test
        @DisplayName("存在的物料應回傳 200 OK 與資料")
        void shouldReturn200WithData() throws Exception {
            MaterialView view = createTestView("MAT-001", "不鏽鋼板", "RAW_MATERIAL");
            when(applicationService.getMaterial("MAT-001")).thenReturn(view);

            mockMvc.perform(get("/api/v1/materials/MAT-001"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data.id").value("MAT-001"))
                    .andExpect(jsonPath("$.data.name").value("不鏽鋼板"))
                    .andExpect(jsonPath("$.data.type").value("RAW_MATERIAL"));
        }

        @Test
        @DisplayName("不存在的物料應回傳 404 Not Found")
        void shouldReturn404WhenNotFound() throws Exception {
            when(applicationService.getMaterial("NOT-EXIST"))
                    .thenThrow(new EntityNotFoundException("Material", "NOT-EXIST"));

            mockMvc.perform(get("/api/v1/materials/NOT-EXIST"))
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("$.code").value(404));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/materials?type= - 依類型查詢")
    class ListByTypeTests {

        @Test
        @DisplayName("應回傳該類型的物料列表")
        void shouldReturnListForType() throws Exception {
            MaterialView view1 = createTestView("MAT-001", "不鏽鋼板", "RAW_MATERIAL");
            MaterialView view2 = createTestView("MAT-002", "銅線", "RAW_MATERIAL");
            when(applicationService.listMaterialsByType("RAW_MATERIAL"))
                    .thenReturn(Arrays.asList(view1, view2));

            mockMvc.perform(get("/api/v1/materials")
                            .param("type", "RAW_MATERIAL"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(2));
        }
    }

    @Nested
    @DisplayName("GET /api/v1/materials/low-stock - 低庫存物料")
    class LowStockTests {

        @Test
        @DisplayName("應回傳低庫存預警列表")
        void shouldReturnLowStockAlerts() throws Exception {
            StockAlertView alert = new StockAlertView("MAT-001", "不鏽鋼板", 5, 20);
            when(applicationService.getLowStockMaterials())
                    .thenReturn(Arrays.asList(alert));

            mockMvc.perform(get("/api/v1/materials/low-stock"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.code").value(200))
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(1))
                    .andExpect(jsonPath("$.data[0].materialId").value("MAT-001"))
                    .andExpect(jsonPath("$.data[0].deficit").value(15));
        }

        @Test
        @DisplayName("無低庫存物料時應回傳空列表")
        void shouldReturnEmptyListWhenNoLowStock() throws Exception {
            when(applicationService.getLowStockMaterials())
                    .thenReturn(Collections.<StockAlertView>emptyList());

            mockMvc.perform(get("/api/v1/materials/low-stock"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data").isArray())
                    .andExpect(jsonPath("$.data.length()").value(0));
        }
    }

    // ========== 輔助方法 ==========

    private MaterialView createTestView(String id, String name, String type) {
        MaterialView view = new MaterialView();
        view.setId(id);
        view.setName(name);
        view.setType(type);
        view.setTypeName("原物料");
        view.setStockQuantity(100);
        view.setUnit("公斤");
        view.setSupplierName("台灣鋼鐵");
        view.setLowStock(false);
        view.setCreatedAt(LocalDateTime.now());
        return view;
    }
}
