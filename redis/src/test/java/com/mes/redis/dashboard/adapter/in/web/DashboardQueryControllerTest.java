package com.mes.redis.dashboard.adapter.in.web;

import com.mes.common.cqrs.QueryBus;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.redis.dashboard.application.query.dto.DashboardView;
import com.mes.redis.dashboard.application.query.dto.LineOverviewView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * DashboardQueryController 查詢控制器測試。
 * 使用 @WebMvcTest 只載入 Web 層，mock QueryBus。
 */
@DisplayName("DashboardQueryController 查詢控制器測試")
@WebMvcTest(DashboardQueryController.class)
class DashboardQueryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QueryBus queryBus;

    @Test
    @DisplayName("GET /api/v1/dashboard/{lineId} 存在時應回傳 200 與看板資料")
    void shouldReturnDashboardData() throws Exception {
        DashboardView view = new DashboardView(
                "LINE-A", 1000, 950, 50,
                new BigDecimal("0.9500"), 120.0,
                new ArrayList<DashboardView.EquipmentStatusView>(),
                LocalDateTime.now());
        when(queryBus.dispatch(any())).thenReturn(view);

        mockMvc.perform(get("/api/v1/dashboard/LINE-A"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.lineId").value("LINE-A"))
                .andExpect(jsonPath("$.data.totalOutput").value(1000))
                .andExpect(jsonPath("$.data.goodCount").value(950));
    }

    @Test
    @DisplayName("GET /api/v1/dashboard/{lineId} 不存在時應回傳 404")
    void shouldReturn404WhenNotFound() throws Exception {
        when(queryBus.dispatch(any()))
                .thenThrow(new EntityNotFoundException("DashboardMetrics", "LINE-X"));

        mockMvc.perform(get("/api/v1/dashboard/LINE-X"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false));
    }

    @Test
    @DisplayName("GET /api/v1/dashboard/overview 應回傳所有產線概覽")
    void shouldReturnAllLinesOverview() throws Exception {
        LineOverviewView overview1 = new LineOverviewView("LINE-A", 1000, new BigDecimal("0.9500"), 3, 4);
        LineOverviewView overview2 = new LineOverviewView("LINE-B", 800, new BigDecimal("0.9200"), 2, 3);
        when(queryBus.dispatch(any())).thenReturn(Arrays.asList(overview1, overview2));

        mockMvc.perform(get("/api/v1/dashboard/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].lineId").value("LINE-A"))
                .andExpect(jsonPath("$.data[1].lineId").value("LINE-B"));
    }

    @Test
    @DisplayName("GET /api/v1/dashboard/overview 無資料時應回傳空列表")
    void shouldReturnEmptyOverview() throws Exception {
        when(queryBus.dispatch(any())).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/v1/dashboard/overview"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}
