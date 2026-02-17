package com.mes.redis.dashboard.application.command;

import com.mes.redis.dashboard.application.query.dto.DashboardView;
import com.mes.redis.dashboard.domain.model.CacheExpiry;
import com.mes.redis.dashboard.domain.model.DashboardMetrics;
import com.mes.redis.dashboard.domain.model.DashboardMetricsId;
import com.mes.redis.dashboard.domain.port.out.CachePort;
import com.mes.redis.dashboard.domain.repository.DashboardMetricsRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * UpdateDashboardCommandHandler 應用層命令處理器測試。
 * 使用 Mock Repository 和 CachePort，驗證 Write-Through 模式。
 */
@DisplayName("UpdateDashboardCommandHandler 命令處理器測試")
class UpdateDashboardCommandHandlerTest {

    private DashboardMetricsRepository repository;
    private CachePort cachePort;
    private UpdateDashboardCommandHandler handler;

    @BeforeEach
    void setUp() {
        repository = mock(DashboardMetricsRepository.class);
        cachePort = mock(CachePort.class);
        handler = new UpdateDashboardCommandHandler(repository, cachePort);
    }

    @Test
    @DisplayName("應新建指標並儲存到 Repository 與快取")
    void shouldCreateNewMetricsAndSaveToRepositoryAndCache() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        UpdateDashboardCommand command = new UpdateDashboardCommand(
                "LINE-A", 1000, 950, 50, 120.0);
        handler.handle(command);

        // 驗證 Write-Through: 同時寫入 Repository 與快取
        verify(repository).save(any(DashboardMetrics.class));
        verify(cachePort).put(eq("dashboard:LINE-A"), any(DashboardView.class), anyLong());
    }

    @Test
    @DisplayName("應更新已有指標並儲存到 Repository 與快取")
    void shouldUpdateExistingMetricsAndSave() {
        DashboardMetrics existing = new DashboardMetrics(
                DashboardMetricsId.of("LINE-A", "20240101"),
                "LINE-A",
                new CacheExpiry(1800, LocalDateTime.now()));
        when(repository.findById(any())).thenReturn(Optional.of(existing));

        UpdateDashboardCommand command = new UpdateDashboardCommand(
                "LINE-A", 2000, 1900, 100, 200.0);
        handler.handle(command);

        verify(repository).save(any(DashboardMetrics.class));
        verify(cachePort).put(anyString(), any(DashboardView.class), anyLong());
    }

    @Test
    @DisplayName("快取鍵應為 'dashboard:' + lineId")
    void shouldUseDashboardPrefixAsCacheKey() {
        when(repository.findById(any())).thenReturn(Optional.empty());

        UpdateDashboardCommand command = new UpdateDashboardCommand(
                "LINE-B", 500, 480, 20, 60.0);
        handler.handle(command);

        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        verify(cachePort).put(keyCaptor.capture(), any(), anyLong());

        assertThat(keyCaptor.getValue()).isEqualTo("dashboard:LINE-B");
    }

    @Test
    @DisplayName("getCommandType 應回傳 UpdateDashboardCommand.class")
    void shouldReturnCorrectCommandType() {
        assertThat(handler.getCommandType()).isEqualTo(UpdateDashboardCommand.class);
    }
}
