package com.mes.redis.dashboard.adapter.in.web;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mes.common.cqrs.CommandBus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * DashboardCommandController 命令控制器測試。
 * 使用 @WebMvcTest 只載入 Web 層，mock CommandBus。
 */
@DisplayName("DashboardCommandController 命令控制器測試")
@WebMvcTest(DashboardCommandController.class)
class DashboardCommandControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CommandBus commandBus;

    @Test
    @DisplayName("POST /api/v1/dashboard/update 應回傳 200 OK")
    void shouldUpdateDashboard() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("lineId", "LINE-A");
        request.put("totalOutput", 1000);
        request.put("goodCount", 950);
        request.put("defectCount", 50);
        request.put("throughputPerHour", 120.5);

        mockMvc.perform(post("/api/v1/dashboard/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Dashboard updated"));

        verify(commandBus).dispatch(any());
    }

    @Test
    @DisplayName("PUT /api/v1/dashboard/{lineId}/equipment 應回傳 200 OK")
    void shouldUpdateEquipmentStatus() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("equipmentId", "EQ-001");
        request.put("equipmentName", "衝壓機A");
        request.put("status", "RUNNING");

        mockMvc.perform(put("/api/v1/dashboard/LINE-A/equipment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Equipment status updated"));

        verify(commandBus).dispatch(any());
    }

    @Test
    @DisplayName("DELETE /api/v1/dashboard/cache/{key} 應回傳 200 OK")
    void shouldInvalidateCache() throws Exception {
        mockMvc.perform(delete("/api/v1/dashboard/cache/test-key"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Cache invalidated"));

        verify(commandBus).dispatch(any());
    }

    @Test
    @DisplayName("CommandBus 拋出例外時應回傳錯誤回應")
    void shouldHandleExceptionFromCommandBus() throws Exception {
        org.mockito.Mockito.when(commandBus.dispatch(any()))
                .thenThrow(new IllegalArgumentException("Invalid lineId"));

        Map<String, Object> request = new HashMap<>();
        request.put("lineId", "");
        request.put("totalOutput", 0);
        request.put("goodCount", 0);
        request.put("defectCount", 0);
        request.put("throughputPerHour", 0);

        mockMvc.perform(post("/api/v1/dashboard/update")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}
