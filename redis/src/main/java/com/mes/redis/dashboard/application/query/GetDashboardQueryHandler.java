package com.mes.redis.dashboard.application.query;

import com.mes.common.cqrs.QueryHandler;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.redis.dashboard.application.assembler.DashboardAssembler;
import com.mes.redis.dashboard.application.query.dto.DashboardView;
import com.mes.redis.dashboard.domain.model.DashboardMetrics;
import com.mes.redis.dashboard.domain.port.out.CachePort;
import com.mes.redis.dashboard.domain.repository.DashboardMetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * [CQRS Pattern: Query Handler - 處理查詢看板指標]
 * [SOLID: SRP - 只負責查詢看板指標的流程]
 * [SOLID: DIP - 依賴 CachePort 抽象與 Repository 抽象]
 *
 * 使用 Cache-Aside（旁路快取）模式：
 * 1. 先從 CachePort 查詢快取
 * 2. 若 cache hit，直接回傳快取的 DashboardView
 * 3. 若 cache miss，從 Repository 載入，轉換為 View，寫入快取，再回傳
 *
 * 教學重點：Cache-Aside Pattern（Lazy Loading）
 * - 讀取時才載入快取，寫入時不主動更新快取
 * - 適合讀取頻繁但寫入不一定需要即時反映的場景
 * - 與 Write-Through 互補使用
 */
@Component
public class GetDashboardQueryHandler implements QueryHandler<GetDashboardQuery, DashboardView> {

    private static final Logger log = LoggerFactory.getLogger(GetDashboardQueryHandler.class);
    private static final String CACHE_KEY_PREFIX = "dashboard:";
    private static final long CACHE_TTL_SECONDS = 1800;

    private final DashboardMetricsRepository repository;
    private final CachePort cachePort;

    public GetDashboardQueryHandler(DashboardMetricsRepository repository, CachePort cachePort) {
        this.repository = repository;
        this.cachePort = cachePort;
    }

    @Override
    public DashboardView handle(GetDashboardQuery query) {
        String cacheKey = CACHE_KEY_PREFIX + query.getLineId();

        // Step 1: 嘗試從快取取得（Cache-Aside 模式）
        DashboardView cached = cachePort.get(cacheKey, DashboardView.class);
        if (cached != null) {
            log.debug("Cache HIT for lineId={}", query.getLineId());
            return cached;
        }

        // Step 2: Cache Miss - 從 Repository 載入
        log.debug("Cache MISS for lineId={}, loading from repository", query.getLineId());
        Optional<DashboardMetrics> metricsOpt = repository.findLatestByLineId(query.getLineId());
        if (!metricsOpt.isPresent()) {
            throw new EntityNotFoundException("DashboardMetrics", query.getLineId());
        }

        // Step 3: 轉換為 View 並寫入快取
        DashboardView view = DashboardAssembler.toView(metricsOpt.get());
        cachePort.put(cacheKey, view, CACHE_TTL_SECONDS);

        log.debug("Dashboard loaded and cached for lineId={}", query.getLineId());
        return view;
    }

    @Override
    public Class<GetDashboardQuery> getQueryType() {
        return GetDashboardQuery.class;
    }
}
