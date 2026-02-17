package com.mes.testing.integration;

import com.mes.testing.application.OrderService;
import com.mes.testing.domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Order 整合測試")
class OrderIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("建立訂單並驗證狀態為 PENDING")
    void createAndVerifyOrder_shouldSucceed() {
        String requestBody = "{\"orderId\":\"IT-001\",\"customerName\":\"Integration Test\",\"amount\":999.99}";

        ResponseEntity<String> response = restTemplate.postForEntity(
                "/api/v1/orders",
                requestBody,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).contains("IT-001");
        assertThat(response.getBody()).contains("PENDING");
    }

    @Test
    @DisplayName("查詢不存在的訂單應該返回 404")
    void getNonExistentOrder_shouldReturn404() {
        ResponseEntity<String> response = restTemplate.getForEntity(
                "/api/v1/orders/NON-EXISTENT",
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    @DisplayName("驗證訂單狀態流：PENDING -> CONFIRMED -> SHIPPED")
    void orderStatusFlow_shouldWork() {
        String orderId = "FLOW-001";
        orderService.createOrder(orderId, "Flow Test", 500.0);

        Order confirmed = orderService.confirmOrder(orderId);
        assertThat(confirmed.getStatus()).isEqualTo(Order.OrderStatus.CONFIRMED);

        Order shipped = orderService.shipOrder(orderId);
        assertThat(shipped.getStatus()).isEqualTo(Order.OrderStatus.SHIPPED);
    }
}
