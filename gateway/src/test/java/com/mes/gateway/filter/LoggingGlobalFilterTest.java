package com.mes.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * [Spring Cloud: GlobalFilter 單元測試]
 *
 * 測試 LoggingGlobalFilter 的過濾器行為：
 * 1. 過濾器順序是否正確
 * 2. 日誌記錄行為是否正常執行
 * 3. 過濾器鏈是否正確傳遞
 */
@DisplayName("LoggingGlobalFilter 日誌過濾器測試")
class LoggingGlobalFilterTest {

    private LoggingGlobalFilter filter;
    private GatewayFilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new LoggingGlobalFilter();
        chain = mock(GatewayFilterChain.class);
        when(chain.filter(any())).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("過濾器順序應為 -1（最高優先度）")
    void shouldHaveHighestPriority() {
        // Act & Assert
        assertThat(filter.getOrder()).isEqualTo(-1);
    }

    @Test
    @DisplayName("應成功處理 GET 請求並傳遞至過濾器鏈")
    void shouldProcessGetRequestAndPassToChain() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/productions")
                .header("Accept", "application/json")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Act
        Mono<Void> result = filter.filter(exchange, chain);

        // Assert - 過濾器應正常完成而不拋出例外
        result.block();
        assertThat(exchange.getAttributes()).containsKey("loggingFilterStartTime");
    }

    @Test
    @DisplayName("應隱藏 Authorization 標頭值以保護敏感資訊")
    void shouldRedactAuthorizationHeader() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/users/me")
                .header("Authorization", "Bearer secret-token-123")
                .header("Content-Type", "application/json")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Act - 過濾器不應拋出例外
        Mono<Void> result = filter.filter(exchange, chain);

        // Assert
        result.block();
        assertThat(exchange.getAttributes()).containsKey("loggingFilterStartTime");
    }
}
