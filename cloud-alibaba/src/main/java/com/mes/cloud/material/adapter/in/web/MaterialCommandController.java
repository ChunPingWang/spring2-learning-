package com.mes.cloud.material.adapter.in.web;

import com.mes.cloud.material.application.MaterialApplicationService;
import com.mes.cloud.material.application.command.ConsumeMaterialCommand;
import com.mes.cloud.material.application.command.ReceiveMaterialCommand;
import com.mes.cloud.material.application.command.RegisterMaterialCommand;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * [Hexagonal Architecture: Inbound Adapter - REST 命令控制器]
 * [CQRS Pattern: Command Side - 只處理寫入操作]
 * [SOLID: SRP - 只負責接收 HTTP 請求並轉發至 ApplicationService]
 * [SOLID: DIP - 依賴 MaterialApplicationService 介面]
 *
 * 物料管理的命令端 REST 控制器。
 * 所有寫入操作透過 MaterialApplicationService 處理，
 * 該服務層整合了 Sentinel 流量控制。
 *
 * 端點設計：
 * - POST /api/v1/materials                  — 註冊新物料
 * - PUT  /api/v1/materials/{id}/receive     — 物料入庫
 * - PUT  /api/v1/materials/{id}/consume     — 物料消耗
 */
@RestController
@RequestMapping("/api/v1/materials")
public class MaterialCommandController {

    private final MaterialApplicationService applicationService;

    public MaterialCommandController(MaterialApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * 註冊新物料。
     *
     * @param command 註冊物料命令
     * @return 新建的物料 ID
     */
    @PostMapping
    public ResponseEntity<ApiResponse<String>> registerMaterial(
            @Valid @RequestBody RegisterMaterialCommand command) {
        String materialId = applicationService.registerMaterial(command);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.created(materialId));
    }

    /**
     * 物料入庫。
     *
     * @param id      物料 ID
     * @param command 入庫命令
     * @return 操作結果
     */
    @PutMapping("/{id}/receive")
    public ResponseEntity<ApiResponse<Void>> receiveMaterial(
            @PathVariable String id,
            @Valid @RequestBody ReceiveMaterialCommand command) {
        command.setMaterialId(id);
        applicationService.receiveMaterial(command);
        return ResponseEntity.ok(ApiResponse.<Void>success("物料入庫成功", null));
    }

    /**
     * 物料消耗。
     *
     * @param id      物料 ID
     * @param command 消耗命令
     * @return 操作結果
     */
    @PutMapping("/{id}/consume")
    public ResponseEntity<ApiResponse<Void>> consumeMaterial(
            @PathVariable String id,
            @Valid @RequestBody ConsumeMaterialCommand command) {
        command.setMaterialId(id);
        applicationService.consumeMaterial(command);
        return ResponseEntity.ok(ApiResponse.<Void>success("物料消耗成功", null));
    }
}
