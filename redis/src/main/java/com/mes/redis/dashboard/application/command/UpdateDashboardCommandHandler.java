package com.mes.redis.dashboard.application.command;

import com.mes.common.cqrs.CommandHandler;
import com.mes.redis.dashboard.application.assembler.DashboardAssembler;
import com.mes.redis.dashboard.application.query.dto.DashboardView;
import com.mes.redis.dashboard.domain.model.CacheExpiry;
import com.mes.redis.dashboard.domain.model.DashboardMetrics;
import com.mes.redis.dashboard.domain.model.DashboardMetricsId;
import com.mes.redis.dashboard.domain.model.ProductionSummary;
import com.mes.redis.dashboard.domain.port.out.CachePort;
import com.mes.redis.dashboard.domain.repository.DashboardMetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * [CQRS Pattern: Command Handler - 處理更新看板指標命令]
 * [SOLID: SRP - 只負責協調更新看板指標的業務流程]
 * [DDD Pattern: Application Service - 協調領域物件完成業務操作]
 *
 * 使用 Write-Through 快取策略：
 * 同時寫入 Repository（主儲存）和 CachePort（快取層），
 * 確保快取與主儲存的一致性。
 *
 * 教學重點：Write-Through Pattern
 * - 寫入時同時更新快取，避免讀取時 cache miss
 * - 適合寫入頻率不高但讀取頻繁的場景
 */
@Component
public class UpdateDashboardCommandHandler implements CommandHandler<UpdateDashboardCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(UpdateDashboardCommandHandler.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int DEFAULT_TTL_SECONDS = 1800;
    private static final String CACHE_KEY_PREFIX = "dashboard:";

    private final DashboardMetricsRepository repository;
    private final CachePort cachePort;

    public UpdateDashboardCommandHandler(DashboardMetricsRepository repository, CachePort cachePort) {
        this.repository = repository;
        this.cachePort = cachePort;
    }

    @Override
    public Void handle(UpdateDashboardCommand command) {
        log.debug("Handling UpdateDashboardCommand for lineId={}", command.getLineId());

        String dateStr = LocalDateTime.now().format(DATE_FORMAT);
        DashboardMetricsId metricsId = DashboardMetricsId.of(command.getLineId(), dateStr);

        // 載入或建立聚合根
        Optional<DashboardMetrics> existing = repository.findById(metricsId);
        DashboardMetrics metrics;
        if (existing.isPresent()) {
            metrics = existing.get();
        } else {
            metrics = new DashboardMetrics(
                    metricsId,
                    command.getLineId(),
                    new CacheExpiry(DEFAULT_TTL_SECONDS, LocalDateTime.now()));
        }

        // 更新生產摘要
        ProductionSummary summary = new ProductionSummary(
                command.getTotalOutput(),
                command.getGoodCount(),
                command.getDefectCount(),
                command.getThroughputPerHour());
        metrics.updateProductionSummary(summary);

        // Write-Through: 同時寫入 Repository 與快取
        repository.save(metrics);

        DashboardView view = DashboardAssembler.toView(metrics);
        cachePort.put(CACHE_KEY_PREFIX + command.getLineId(), view, DEFAULT_TTL_SECONDS);

        log.info("Dashboard updated and cached for lineId={}", command.getLineId());
        return null;
    }

    @Override
    public Class<UpdateDashboardCommand> getCommandType() {
        return UpdateDashboardCommand.class;
    }
}
