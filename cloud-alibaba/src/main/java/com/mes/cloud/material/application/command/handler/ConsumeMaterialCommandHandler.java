package com.mes.cloud.material.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.ddd.event.DomainEventPublisher;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.cloud.material.application.command.ConsumeMaterialCommand;
import com.mes.cloud.material.domain.Material;
import com.mes.cloud.material.domain.MaterialId;
import com.mes.cloud.material.domain.repository.MaterialRepository;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Command Handler - 物料消耗]
 * [SOLID: SRP - 只負責處理 ConsumeMaterialCommand]
 * [Hexagonal Architecture: Application Service - 協調 Domain Model 與 Infrastructure]
 *
 * 載入物料聚合根，呼叫 consume() 方法扣減庫存，儲存後發佈領域事件。
 * 消耗操作可能產生 MaterialConsumedEvent 和 LowStockAlertEvent。
 */
@Component
public class ConsumeMaterialCommandHandler
        implements CommandHandler<ConsumeMaterialCommand, Void> {

    private final MaterialRepository repository;
    private final DomainEventPublisher eventPublisher;

    public ConsumeMaterialCommandHandler(MaterialRepository repository,
                                          DomainEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Void handle(ConsumeMaterialCommand command) {
        // 1. 載入聚合根
        MaterialId materialId = MaterialId.of(command.getMaterialId());
        Material material = repository.findById(materialId)
                .orElseThrow(() -> new EntityNotFoundException("Material", command.getMaterialId()));

        // 2. 執行業務操作
        material.consume(command.getQuantity(), command.getWorkOrderId());

        // 3. 持久化
        repository.save(material);

        // 4. 發佈領域事件
        for (DomainEvent event : material.getDomainEvents()) {
            eventPublisher.publish(event);
        }
        material.clearEvents();

        return null;
    }

    @Override
    public Class<ConsumeMaterialCommand> getCommandType() {
        return ConsumeMaterialCommand.class;
    }
}
