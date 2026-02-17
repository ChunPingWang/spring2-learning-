package com.mes.redis.dashboard.application.query;

import com.mes.common.exception.EntityNotFoundException;
import com.mes.redis.dashboard.application.query.dto.DashboardView;
import com.mes.redis.dashboard.domain.model.CacheExpiry;
import com.mes.redis.dashboard.domain.model.DashboardMetrics;
import com.mes.redis.dashboard.domain.model.DashboardMetricsId;
import com.mes.redis.dashboard.domain.model.ProductionSummary;
import com.mes.redis.dashboard.domain.port.out.CachePort;
import com.mes.redis.dashboard.domain.repository.DashboardMetricsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * GetDashboardQueryHandler 查詢處理器測試。
 * 驗證 Cache-Aside 模式：cache hit / cache miss 路徑。
 */
@DisplayName("GetDashboardQueryHandler 查詢處理器測試")
class GetDashboardQueryHandlerTest {

    private DashboardMetricsRepository repository;
    private CachePort cachePort;
    private GetDashboardQueryHandler handler;

    @BeforeEach
    void setUp() {
        repository = mock(DashboardMetricsRepository.class);
        cachePort = mock(CachePort.class);
        handler = new GetDashboardQueryHandler(repository, cachePort);
    }

    @Test
    @DisplayName("Cache Hit 時應直接回傳快取值，不查 Repository")
    void shouldReturnCachedValueOnCacheHit() {
        DashboardView cachedView = new DashboardView();
        cachedView.setLineId("LINE-A");
        cachedView.setTotalOutput(1000);
        when(cachePort.get("dashboard:LINE-A", DashboardView.class)).thenReturn(cachedView);

        GetDashboardQuery query = new GetDashboardQuery("LINE-A");
        DashboardView result = handler.handle(query);

        assertThat(result.getLineId()).isEqualTo("LINE-A");
        assertThat(result.getTotalOutput()).isEqualTo(1000);

        // 驗證沒有查詢 Repository
        verify(repository, never()).findLatestByLineId(anyString());
    }

    @Test
    @DisplayName("Cache Miss 時應從 Repository 載入並寫入快取")
    void shouldLoadFromRepositoryOnCacheMiss() {
        when(cachePort.get("dashboard:LINE-A", DashboardView.class)).thenReturn(null);

        DashboardMetrics metrics = new DashboardMetrics(
                DashboardMetricsId.of("LINE-A", "20240101"),
                "LINE-A",
                new CacheExpiry(1800, LocalDateTime.now()));
        metrics.updateProductionSummary(new ProductionSummary(500, 480, 20, 60.0));
        when(repository.findLatestByLineId("LINE-A")).thenReturn(Optional.of(metrics));

        GetDashboardQuery query = new GetDashboardQuery("LINE-A");
        DashboardView result = handler.handle(query);

        assertThat(result.getLineId()).isEqualTo("LINE-A");
        assertThat(result.getTotalOutput()).isEqualTo(500);

        // 驗證寫入快取
        verify(cachePort).put(eq("dashboard:LINE-A"), any(DashboardView.class), anyLong());
    }

    @Test
    @DisplayName("Cache Miss 且 Repository 無資料時應拋出 EntityNotFoundException")
    void shouldThrowExceptionWhenNotFoundAnywhere() {
        when(cachePort.get("dashboard:LINE-X", DashboardView.class)).thenReturn(null);
        when(repository.findLatestByLineId("LINE-X")).thenReturn(Optional.empty());

        GetDashboardQuery query = new GetDashboardQuery("LINE-X");

        assertThatThrownBy(() -> handler.handle(query))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("LINE-X");
    }

    @Test
    @DisplayName("getQueryType 應回傳 GetDashboardQuery.class")
    void shouldReturnCorrectQueryType() {
        assertThat(handler.getQueryType()).isEqualTo(GetDashboardQuery.class);
    }

    @Test
    @DisplayName("快取鍵格式應為 'dashboard:' + lineId")
    void shouldUseDashboardPrefixAsCacheKey() {
        DashboardView cachedView = new DashboardView();
        cachedView.setLineId("LINE-C");
        when(cachePort.get("dashboard:LINE-C", DashboardView.class)).thenReturn(cachedView);

        GetDashboardQuery query = new GetDashboardQuery("LINE-C");
        handler.handle(query);

        verify(cachePort).get(eq("dashboard:LINE-C"), eq(DashboardView.class));
    }
}
