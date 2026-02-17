package com.mes.testing.hateoas;

import com.mes.testing.application.OrderService;
import com.mes.testing.domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderHateoasController.class)
@DisplayName("HATEOAS 控制器測試")
class OrderHateoasControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private OrderService orderService;

    @MockBean
    private OrderAssembler orderAssembler;

    @Test
    @DisplayName("GET /api/v1/hateoas/orders - 應該包含 HATEOAS 連結")
    void getAllOrders_shouldIncludeHateoasLinks() throws Exception {
        when(orderService.getAllOrders()).thenReturn(Arrays.asList(
                new Order("ORD-001", "Customer A", 100.0)
        ));
        when(orderAssembler.toModel(any(Order.class))).thenReturn(
                EntityModel.of(new Order("ORD-001", "Customer A", 100.0))
        );
        when(orderAssembler.getLinks()).thenReturn(new RepresentationModel<>());

        mockMvc.perform(get("/api/v1/hateoas/orders"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/v1/hateoas/orders/{orderId} - 應該包含自我連結")
    void getOrder_shouldIncludeSelfLink() throws Exception {
        Order order = new Order("ORD-001", "Customer A", 100.0);
        when(orderService.getOrder("ORD-001")).thenReturn(order);
        when(orderAssembler.toModel(order)).thenReturn(EntityModel.of(order));

        mockMvc.perform(get("/api/v1/hateoas/orders/ORD-001"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /api/v1/hateoas/orders - 應該返回 201 並包含連結")
    void createOrder_shouldReturn201WithLinks() throws Exception {
        Order order = new Order("ORD-NEW", "New Customer", 500.0);
        when(orderService.createOrder(anyString(), anyString(), anyDouble())).thenReturn(order);
        when(orderAssembler.toModel(order)).thenReturn(EntityModel.of(order));

        mockMvc.perform(post("/api/v1/hateoas/orders")
                        .contentType("application/json")
                        .content("{\"orderId\":\"ORD-NEW\",\"customerName\":\"New Customer\",\"amount\":500.0}"))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("GET /api/v1/hateoas/orders?status=PENDING - 應該過濾狀態")
    void getOrdersByStatus_shouldFilter() throws Exception {
        Order pendingOrder = new Order("ORD-001", "Customer A", 100.0);
        when(orderService.getOrdersByStatus(Order.OrderStatus.PENDING)).thenReturn(Arrays.asList(pendingOrder));
        when(orderAssembler.toModel(pendingOrder)).thenReturn(EntityModel.of(pendingOrder));

        mockMvc.perform(get("/api/v1/hateoas/orders").param("status", "PENDING"))
                .andExpect(status().isOk());
    }
}
