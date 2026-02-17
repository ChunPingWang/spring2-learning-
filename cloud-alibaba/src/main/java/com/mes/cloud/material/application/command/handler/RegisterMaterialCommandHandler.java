package com.mes.cloud.material.application.command.handler;

import com.mes.common.cqrs.CommandHandler;
import com.mes.cloud.material.application.command.RegisterMaterialCommand;
import com.mes.cloud.material.domain.Material;
import com.mes.cloud.material.domain.MaterialId;
import com.mes.cloud.material.domain.MaterialType;
import com.mes.cloud.material.domain.MaterialUnit;
import com.mes.cloud.material.domain.StockLevel;
import com.mes.cloud.material.domain.Supplier;
import com.mes.cloud.material.domain.repository.MaterialRepository;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Command Handler - 註冊新物料]
 * [SOLID: SRP - 只負責處理 RegisterMaterialCommand]
 * [SOLID: OCP - 新增 Command 只需新增 Handler，不修改此類別]
 * [Hexagonal Architecture: Application Service - 協調 Domain Model 與 Infrastructure]
 *
 * 接收 RegisterMaterialCommand，建立新的 Material 聚合根，
 * 並透過 Repository 持久化。
 */
@Component
public class RegisterMaterialCommandHandler
        implements CommandHandler<RegisterMaterialCommand, String> {

    private final MaterialRepository repository;

    public RegisterMaterialCommandHandler(MaterialRepository repository) {
        this.repository = repository;
    }

    @Override
    public String handle(RegisterMaterialCommand command) {
        // 1. 建構領域物件
        MaterialId materialId = MaterialId.generate();
        MaterialType type = MaterialType.valueOf(command.getMaterialType());
        MaterialUnit unit = new MaterialUnit(command.getUnitCode(), command.getUnitName());
        StockLevel stockLevel = new StockLevel(command.getInitialStock(), command.getUnitCode());
        Supplier supplier = new Supplier(
                command.getSupplierId(),
                command.getSupplierName(),
                command.getContactInfo());

        // 2. 建立聚合根
        Material material = new Material(
                materialId, command.getName(), type, unit,
                stockLevel, command.getMinimumStock(), supplier);

        // 3. 透過 Repository 持久化
        repository.save(material);

        // 4. 回傳新建的物料 ID
        return materialId.getValue();
    }

    @Override
    public Class<RegisterMaterialCommand> getCommandType() {
        return RegisterMaterialCommand.class;
    }
}
