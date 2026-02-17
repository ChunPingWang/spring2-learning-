package com.mes.redis.dashboard.infrastructure.persistence;

import com.mes.redis.dashboard.domain.model.DashboardMetrics;
import com.mes.redis.dashboard.domain.model.DashboardMetricsId;
import com.mes.redis.dashboard.domain.repository.DashboardMetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [DDD Pattern: Repository - Adapter (出站配接器)]
 * [SOLID: LSP - 完整實作 DashboardMetricsRepository 介面]
 * [SOLID: DIP - 領域層依賴抽象，此類別是具體實作]
 * [Hexagonal Architecture: Output Adapter - 持久化配接器]
 *
 * 使用記憶體（ConcurrentHashMap）儲存的 DashboardMetricsRepository 實作。
 * 適用於開發測試環境，生產環境可替換為 Redis 或 JPA 實作。
 */
@Component
public class InMemoryDashboardRepository implements DashboardMetricsRepository {

    private static final Logger log = LoggerFactory.getLogger(InMemoryDashboardRepository.class);

    private final Map<DashboardMetricsId, DashboardMetrics> store = new ConcurrentHashMap<>();

    @Override
    public Optional<DashboardMetrics> findById(DashboardMetricsId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<DashboardMetrics> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public void save(DashboardMetrics aggregate) {
        store.put(aggregate.getId(), aggregate);
        log.debug("Saved DashboardMetrics: id={}", aggregate.getId().getValue());
    }

    @Override
    public void deleteById(DashboardMetricsId id) {
        store.remove(id);
        log.debug("Deleted DashboardMetrics: id={}", id.getValue());
    }

    @Override
    public List<DashboardMetrics> findByLineId(String lineId) {
        List<DashboardMetrics> result = new ArrayList<>();
        for (DashboardMetrics metrics : store.values()) {
            if (lineId.equals(metrics.getLineId())) {
                result.add(metrics);
            }
        }
        return result;
    }

    @Override
    public Optional<DashboardMetrics> findLatestByLineId(String lineId) {
        DashboardMetrics latest = null;
        for (DashboardMetrics metrics : store.values()) {
            if (lineId.equals(metrics.getLineId())) {
                if (latest == null || metrics.getSnapshotTime().isAfter(latest.getSnapshotTime())) {
                    latest = metrics;
                }
            }
        }
        return Optional.ofNullable(latest);
    }
}
