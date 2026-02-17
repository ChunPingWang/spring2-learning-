package com.mes.redis.dashboard.application.command;

import com.mes.common.cqrs.CommandHandler;
import com.mes.redis.dashboard.domain.model.CacheExpiry;
import com.mes.redis.dashboard.domain.model.DashboardMetrics;
import com.mes.redis.dashboard.domain.model.DashboardMetricsId;
import com.mes.redis.dashboard.domain.model.EquipmentStatusSnapshot;
import com.mes.redis.dashboard.domain.repository.DashboardMetricsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * [CQRS Pattern: Command Handler - 處理更新設備狀態命令]
 * [SOLID: SRP - 只負責協調更新設備狀態的業務流程]
 *
 * 載入指定產線的看板指標聚合根，
 * 新增或更新設備狀態快照，然後儲存回 Repository。
 */
@Component
public class UpdateEquipmentStatusCommandHandler
        implements CommandHandler<UpdateEquipmentStatusCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(UpdateEquipmentStatusCommandHandler.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final int DEFAULT_TTL_SECONDS = 1800;

    private final DashboardMetricsRepository repository;

    public UpdateEquipmentStatusCommandHandler(DashboardMetricsRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void handle(UpdateEquipmentStatusCommand command) {
        log.debug("Handling UpdateEquipmentStatusCommand for lineId={}, equipmentId={}",
                command.getLineId(), command.getEquipmentId());

        String dateStr = LocalDateTime.now().format(DATE_FORMAT);
        DashboardMetricsId metricsId = DashboardMetricsId.of(command.getLineId(), dateStr);

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

        // 嘗試更新已存在的設備狀態，若不存在則新增
        EquipmentStatusSnapshot snapshot = new EquipmentStatusSnapshot(
                command.getEquipmentId(),
                command.getEquipmentName(),
                command.getStatus(),
                LocalDateTime.now());
        metrics.addEquipmentStatus(snapshot);

        repository.save(metrics);
        log.info("Equipment status updated for lineId={}, equipmentId={}",
                command.getLineId(), command.getEquipmentId());

        return null;
    }

    @Override
    public Class<UpdateEquipmentStatusCommand> getCommandType() {
        return UpdateEquipmentStatusCommand.class;
    }
}
