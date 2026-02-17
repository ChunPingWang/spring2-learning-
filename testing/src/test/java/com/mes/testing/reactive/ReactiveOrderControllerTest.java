package com.mes.testing.reactive;

import com.mes.testing.domain.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureWebTestClient
@DisplayName("WebFlux 響應式控制器測試")
class ReactiveOrderControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("GET /api/v1/reactive/orders - 應該返回訂單列表")
    void getAllOrders_shouldReturnList() {
        webTestClient.get().uri("/api/v1/reactive/orders")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$").isArray();
    }

    @Test
    @DisplayName("POST /api/v1/reactive/orders - 應該成功建立訂單")
    void createOrder_shouldReturnCreated() {
        webTestClient.post().uri("/api/v1/reactive/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"orderId\":\"ORD-TEST-001\",\"customerName\":\"Test Customer\",\"amount\":500.0}")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.orderId").isEqualTo("ORD-TEST-001")
                .jsonPath("$.customerName").isEqualTo("Test Customer");
    }

    @Test
    @DisplayName("GET /api/v1/reactive/orders/{orderId} - 應該返回指定訂單")
    void getOrder_shouldReturnOrder() {
        webTestClient.post().uri("/api/v1/reactive/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"orderId\":\"ORD-002\",\"customerName\":\"Customer B\",\"amount\":200.0}")
                .exchange();

        webTestClient.get().uri("/api/v1/reactive/orders/ORD-002")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.orderId").isEqualTo("ORD-002");
    }

    @Test
    @DisplayName("GET /api/v1/reactive/orders/{orderId} - 訂單不存在應該回傳錯誤")
    void getOrder_notFound_shouldReturnError() {
        webTestClient.get().uri("/api/v1/reactive/orders/NON-EXISTENT")
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    @DisplayName("GET /api/v1/reactive/orders/stream - 應該返回 Server-Sent Events 流")
    void streamOrders_shouldReturnSSE() {
        webTestClient.get().uri("/api/v1/reactive/orders/stream")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM);
    }

    @Test
    @DisplayName("GET /api/v1/reactive/orders/count - 應該返回訂單數量")
    void getOrderCount_shouldReturnCount() {
        webTestClient.get().uri("/api/v1/reactive/orders/count")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.count").isNumber();
    }

    @Test
    @DisplayName("DELETE /api/v1/reactive/orders/{orderId} - 應該成功刪除訂單")
    void deleteOrder_shouldSucceed() {
        webTestClient.post().uri("/api/v1/reactive/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"orderId\":\"ORD-DELETE\",\"customerName\":\"To Delete\",\"amount\":100.0}")
                .exchange();

        webTestClient.delete().uri("/api/v1/reactive/orders/ORD-DELETE")
                .exchange()
                .expectStatus().isOk();
    }
}
