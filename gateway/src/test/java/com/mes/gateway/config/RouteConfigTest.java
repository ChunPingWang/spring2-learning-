package com.mes.gateway.config;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * [Spring Cloud: Gateway 路由配置整合測試]
 *
 * 測試 Gateway 路由配置是否正確載入：
 * 1. RouteLocator Bean 是否存在
 * 2. 所有預期路由是否已註冊
 * 3. 下游服務不可用時是否正確觸發降級（fallback）
 * 4. 斷路器是否配置在路由上
 * 5. 不存在的路由是否回傳 404
 */
@DisplayName("RouteConfig 閘道路由配置測試")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient(timeout = "10000")
class RouteConfigTest {

    @Autowired
    private WebTestClient webTestClient;

    @Autowired
    private RouteLocator routeLocator;

    @Test
    @DisplayName("RouteLocator Bean 應成功載入")
    void routeLocatorShouldBeLoaded() {
        assertThat(routeLocator).isNotNull();
    }

    @Test
    @DisplayName("應包含所有預期的路由定義")
    void shouldContainAllExpectedRoutes() {
        // Act
        long routeCount = routeLocator.getRoutes()
                .filter(route -> route.getId().contains("service"))
                .count()
                .block();

        // Assert - 應有 6 個服務路由（production, equipment, inspection, user, auth, dashboard）
        assertThat(routeCount).isGreaterThanOrEqualTo(6);
    }

    @Test
    @DisplayName("生產服務路由在下游不可用時應觸發降級回應")
    void productionRouteShouldFallbackWhenServiceUnavailable() {
        // Act & Assert
        // 因為下游 8081 服務未啟動，斷路器應將請求導向 fallback
        webTestClient.get().uri("/api/v1/productions")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("生產服務暫時不可用");
    }

    @Test
    @DisplayName("設備服務路由在下游不可用時應觸發降級回應")
    void equipmentRouteShouldFallbackWhenServiceUnavailable() {
        webTestClient.get().uri("/api/v1/equipment")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.message").isEqualTo("設備服務暫時不可用");
    }

    @Test
    @DisplayName("不存在的路徑應回傳 404")
    void unknownPathShouldReturn404() {
        webTestClient.get().uri("/api/v1/unknown-service")
                .exchange()
                .expectStatus().isNotFound();
    }
}
