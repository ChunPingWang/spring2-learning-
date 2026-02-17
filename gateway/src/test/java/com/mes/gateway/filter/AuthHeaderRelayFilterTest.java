package com.mes.gateway.filter;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * [Spring Cloud: GlobalFilter 單元測試]
 *
 * 測試 AuthHeaderRelayFilter 的認證標頭轉發行為：
 * 1. 有 Authorization 標頭時應正確轉發
 * 2. 無 Authorization 標頭時應直接放行
 * 3. 過濾器順序是否正確
 */
@DisplayName("AuthHeaderRelayFilter 認證標頭轉發過濾器測試")
class AuthHeaderRelayFilterTest {

    private AuthHeaderRelayFilter filter;
    private GatewayFilterChain chain;

    @BeforeEach
    void setUp() {
        filter = new AuthHeaderRelayFilter();
        chain = mock(GatewayFilterChain.class);
        when(chain.filter(any(ServerWebExchange.class))).thenReturn(Mono.empty());
    }

    @Test
    @DisplayName("有 Authorization 標頭時應轉發至下游")
    void shouldRelayAuthorizationHeader() {
        // Arrange
        String token = "Bearer eyJhbGciOiJIUzI1NiJ9.test-token";
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/productions")
                .header(HttpHeaders.AUTHORIZATION, token)
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Act
        filter.filter(exchange, chain).block();

        // Assert - chain.filter 應被呼叫（表示請求被傳遞）
        verify(chain).filter(any(ServerWebExchange.class));
    }

    @Test
    @DisplayName("無 Authorization 標頭時應直接放行不報錯")
    void shouldPassThroughWhenNoAuthHeader() {
        // Arrange
        MockServerHttpRequest request = MockServerHttpRequest
                .get("/api/v1/productions")
                .build();
        MockServerWebExchange exchange = MockServerWebExchange.from(request);

        // Act
        filter.filter(exchange, chain).block();

        // Assert - 應直接傳遞原始 exchange
        verify(chain).filter(exchange);
    }

    @Test
    @DisplayName("過濾器順序應為 0")
    void shouldHaveOrderZero() {
        // Act & Assert
        assertThat(filter.getOrder()).isEqualTo(0);
    }
}
