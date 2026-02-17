package com.mes.gateway.aggregation;

import com.mes.gateway.aggregation.dto.AggregatedDashboardView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * [Spring Cloud: 服務聚合處理器單元測試]
 *
 * 使用 Mock WebClient 測試 DashboardAggregationHandler：
 * 1. 兩個服務都正常時應聚合成功
 * 2. 單一服務失敗時應部分降級
 * 3. 全部服務失敗時應全部降級
 */
@DisplayName("DashboardAggregationHandler 儀表板聚合測試")
@SuppressWarnings("unchecked")
class DashboardAggregationHandlerTest {

    private WebClient webClient;
    private WebClient.RequestHeadersUriSpec<?> requestHeadersUriSpec;
    private WebClient.RequestHeadersSpec<?> requestHeadersSpec;
    private WebClient.ResponseSpec responseSpec;
    private DashboardAggregationHandler handler;

    @BeforeEach
    void setUp() {
        webClient = mock(WebClient.class);
        requestHeadersUriSpec = mock(WebClient.RequestHeadersUriSpec.class);
        requestHeadersSpec = mock(WebClient.RequestHeadersSpec.class);
        responseSpec = mock(WebClient.ResponseSpec.class);

        when(webClient.get()).thenReturn((WebClient.RequestHeadersUriSpec) requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(anyString())).thenReturn((WebClient.RequestHeadersSpec) requestHeadersSpec);
        when(requestHeadersSpec.retrieve()).thenReturn(responseSpec);

        handler = new DashboardAggregationHandler(webClient);
    }

    @Test
    @DisplayName("兩個服務都正常時應成功聚合儀表板資料")
    void shouldAggregateWhenBothServicesAvailable() {
        // Arrange
        Map<String, Object> serviceData = new HashMap<>();
        serviceData.put("totalOrders", 100);
        serviceData.put("activeLines", 5);

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(serviceData));

        // Act & Assert
        StepVerifier.create(handler.aggregateDashboard())
                .assertNext(view -> {
                    assertThat(view).isNotNull();
                    assertThat(view.getTimestamp()).isNotNull();
                    assertThat(view.getProductionSummary()).isNotNull();
                    assertThat(view.getEquipmentStatus()).isNotNull();
                    assertThat(view.getProductionSummary()).containsKey("totalOrders");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("下游服務連線失敗時應回傳降級資料")
    void shouldReturnFallbackWhenServiceConnectionFails() {
        // Arrange - 模擬連線失敗，onErrorResume 應捕捉並回傳降級資料
        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.error(new RuntimeException("Connection refused")));

        // Act & Assert
        StepVerifier.create(handler.aggregateDashboard())
                .assertNext(view -> {
                    assertThat(view).isNotNull();
                    assertThat(view.getTimestamp()).isNotNull();
                    // 降級時應包含狀態為 unavailable
                    assertThat(view.getProductionSummary()).containsEntry("status", "unavailable");
                    assertThat(view.getEquipmentStatus()).containsEntry("status", "unavailable");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("聚合結果應包含時間戳")
    void shouldIncludeTimestampInAggregatedResult() {
        // Arrange
        Map<String, Object> data = new HashMap<>();
        data.put("count", 42);

        when(responseSpec.bodyToMono(any(ParameterizedTypeReference.class)))
                .thenReturn(Mono.just(data));

        // Act & Assert
        StepVerifier.create(handler.aggregateDashboard())
                .assertNext(view -> {
                    assertThat(view.getTimestamp()).isNotNull();
                    assertThat(view.getTimestamp()).isNotEmpty();
                })
                .verifyComplete();
    }
}
