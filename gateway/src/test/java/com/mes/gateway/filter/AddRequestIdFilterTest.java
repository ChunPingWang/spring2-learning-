package com.mes.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * [Spring Cloud: GatewayFilterFactory 單元測試]
 *
 * 測試 AddRequestIdFilter 的自訂過濾器行為：
 * 1. 應為請求新增 X-Request-Id UUID 標頭
 * 2. 可透過 Config 自訂標頭名稱
 */
@DisplayName("AddRequestIdFilter 請求 ID 過濾器測試")
class AddRequestIdFilterTest {

    private AddRequestIdFilter filterFactory;

    @BeforeEach
    void setUp() {
        filterFactory = new AddRequestIdFilter();
    }

    @Test
    @DisplayName("應為請求新增 X-Request-Id UUID 標頭")
    void shouldAddRequestIdHeader() {
        // Arrange
        AddRequestIdFilter.Config config = new AddRequestIdFilter.Config();
        GatewayFilter filter = filterFactory.apply(config);

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/productions")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        AtomicReference<ServerWebExchange> capturedExchange = new AtomicReference<>();
        GatewayFilterChain chain = ex -> {
            capturedExchange.set(ex);
            return Mono.empty();
        };

        // Act
        filter.filter(exchange, chain).block();

        // Assert
        assertThat(capturedExchange.get()).isNotNull();
        String requestId = capturedExchange.get().getRequest().getHeaders()
                .getFirst("X-Request-Id");
        assertThat(requestId).isNotNull();
        assertThat(requestId).isNotEmpty();
        // UUID 格式驗證：8-4-4-4-12
        assertThat(requestId).matches(
                "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }

    @Test
    @DisplayName("應支援透過 Config 自訂標頭名稱")
    void shouldSupportCustomHeaderName() {
        // Arrange
        AddRequestIdFilter.Config config = new AddRequestIdFilter.Config();
        config.setHeaderName("X-Trace-Id");
        GatewayFilter filter = filterFactory.apply(config);

        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/equipment")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        AtomicReference<ServerWebExchange> capturedExchange = new AtomicReference<>();
        GatewayFilterChain chain = ex -> {
            capturedExchange.set(ex);
            return Mono.empty();
        };

        // Act
        filter.filter(exchange, chain).block();

        // Assert
        assertThat(capturedExchange.get()).isNotNull();
        String traceId = capturedExchange.get().getRequest().getHeaders()
                .getFirst("X-Trace-Id");
        assertThat(traceId).isNotNull();
        assertThat(traceId).matches(
                "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
    }
}
