package com.mes.gateway.fallback;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * [Spring Cloud: 斷路器降級端點整合測試]
 *
 * 使用 WebTestClient 測試降級端點是否回傳友善的中文錯誤訊息。
 * 驗證：
 * 1. 生產服務降級回應
 * 2. 設備服務降級回應
 * 3. 通用降級回應
 */
@DisplayName("FallbackController 斷路器降級端點測試")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class FallbackControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    @DisplayName("生產服務降級應回傳「生產服務暫時不可用」")
    void productionFallbackShouldReturnGracefulMessage() {
        webTestClient.get().uri("/fallback/production")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(503)
                .jsonPath("$.message").isEqualTo("生產服務暫時不可用")
                .jsonPath("$.service").isEqualTo("production-service")
                .jsonPath("$.timestamp").isNotEmpty();
    }

    @Test
    @DisplayName("設備服務降級應回傳「設備服務暫時不可用」")
    void equipmentFallbackShouldReturnGracefulMessage() {
        webTestClient.get().uri("/fallback/equipment")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(503)
                .jsonPath("$.message").isEqualTo("設備服務暫時不可用")
                .jsonPath("$.service").isEqualTo("equipment-service")
                .jsonPath("$.timestamp").isNotEmpty();
    }

    @Test
    @DisplayName("通用降級應回傳「服務暫時不可用，請稍後重試」")
    void generalFallbackShouldReturnGracefulMessage() {
        webTestClient.post().uri("/fallback/general")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.code").isEqualTo(503)
                .jsonPath("$.message").isEqualTo("服務暫時不可用，請稍後重試")
                .jsonPath("$.service").isEqualTo("general")
                .jsonPath("$.timestamp").isNotEmpty();
    }
}
