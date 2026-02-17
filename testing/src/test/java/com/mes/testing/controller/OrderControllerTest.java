package com.mes.testing.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mes.testing.adapter.in.web.OrderController;
import com.mes.testing.application.OrderService;
import com.mes.testing.domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@DisplayName("OrderController MockMvc 測試")
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private OrderService orderService;

    @Test
    @DisplayName("POST /api/v1/orders - 應該成功建立訂單")
    void createOrder_shouldReturn201() throws Exception {
        Order order = new Order("ORD-001", "Customer A", 100.0);
        when(orderService.createOrder(anyString(), anyString(), anyDouble())).thenReturn(order);

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":\"ORD-001\",\"customerName\":\"Customer A\",\"amount\":100.0}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.orderId").value("ORD-001"))
                .andExpect(jsonPath("$.customerName").value("Customer A"))
                .andExpect(jsonPath("$.amount").value(100.0))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @DisplayName("POST /api/v1/orders - 驗證失敗應該返回 400")
    void createOrder_invalidInput_shouldReturn400() throws Exception {
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderId\":\"\",\"customerName\":\"\",\"amount\":-10}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/v1/orders - 應該返回所有訂單")
    void getAllOrders_shouldReturnList() throws Exception {
        List<Order> orders = Arrays.asList(
                new Order("ORD-001", "Customer A", 100.0),
                new Order("ORD-002", "Customer B", 200.0)
        );
        when(orderService.getAllOrders()).thenReturn(orders);

        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].orderId").value("ORD-001"))
                .andExpect(jsonPath("$[1].orderId").value("ORD-002"));
    }

    @Test
    @DisplayName("GET /api/v1/orders/{orderId} - 應該返回指定訂單")
    void getOrder_shouldReturnOrder() throws Exception {
        Order order = new Order("ORD-001", "Customer A", 100.0);
        when(orderService.getOrder("ORD-001")).thenReturn(order);

        mockMvc.perform(get("/api/v1/orders/ORD-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("ORD-001"));
    }

    @Test
    @DisplayName("GET /api/v1/orders/{orderId} - 訂單不存在應該返回 404")
    void getOrder_notFound_shouldReturn404() throws Exception {
        when(orderService.getOrder("ORD-999")).thenThrow(new IllegalArgumentException("Order not found"));

        mockMvc.perform(get("/api/v1/orders/ORD-999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /api/v1/orders/{orderId}/confirm - 應該成功確認訂單")
    void confirmOrder_shouldSucceed() throws Exception {
        Order order = new Order("ORD-001", "Customer A", 100.0);
        order.confirm();
        when(orderService.confirmOrder("ORD-001")).thenReturn(order);

        mockMvc.perform(post("/api/v1/orders/ORD-001/confirm"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    @DisplayName("GET /api/v1/orders?status=PENDING - 應該過濾狀態")
    void getOrdersByStatus_shouldFilter() throws Exception {
        Order pendingOrder = new Order("ORD-001", "Customer A", 100.0);
        when(orderService.getOrdersByStatus(Order.OrderStatus.PENDING))
                .thenReturn(Arrays.asList(pendingOrder));

        mockMvc.perform(get("/api/v1/orders").param("status", "PENDING"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("PENDING"));
    }
}
