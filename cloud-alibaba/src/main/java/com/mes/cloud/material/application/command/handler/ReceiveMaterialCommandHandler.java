package com.mes.cloud.material.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.cloud.material.application.command.ReceiveMaterialCommand;
import com.mes.cloud.material.domain.Material;
import com.mes.cloud.material.domain.MaterialId;
import com.mes.cloud.material.domain.repository.MaterialRepository;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Command Handler - 物料入庫]
 * [SOLID: SRP - 只負責處理 ReceiveMaterialCommand]
 * [Hexagonal Architecture: Application Service - 協調 Domain Model 與 Infrastructure]
 *
 * 載入物料聚合根，呼叫 receive() 方法增加庫存，並儲存。
 */
@Component
public class ReceiveMaterialCommandHandler
        implements CommandHandler<ReceiveMaterialCommand, Void> {

    private final MaterialRepository repository;

    public ReceiveMaterialCommandHandler(MaterialRepository repository) {
        this.repository = repository;
    }

    @Override
    public Void handle(ReceiveMaterialCommand command) {
        // 1. 載入聚合根
        MaterialId materialId = MaterialId.of(command.getMaterialId());
        Material material = repository.findById(materialId)
                .orElseThrow(() -> new EntityNotFoundException("Material", command.getMaterialId()));

        // 2. 執行業務操作
        material.receive(command.getQuantity());

        // 3. 持久化
        repository.save(material);

        return null;
    }

    @Override
    public Class<ReceiveMaterialCommand> getCommandType() {
        return ReceiveMaterialCommand.class;
    }
}
