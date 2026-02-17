package com.mes.cloud.material.adapter.in.web;

import com.mes.cloud.material.application.MaterialApplicationService;
import com.mes.cloud.material.application.query.dto.MaterialView;
import com.mes.cloud.material.application.query.dto.StockAlertView;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * [Hexagonal Architecture: Inbound Adapter - REST 查詢控制器]
 * [CQRS Pattern: Query Side - 只處理讀取操作]
 * [SOLID: SRP - 只負責接收 HTTP 請求並轉發至 ApplicationService]
 * [SOLID: ISP - 與 CommandController 分離，遵循介面隔離原則]
 *
 * 物料管理的查詢端 REST 控制器。
 * 所有讀取操作透過 MaterialApplicationService 處理。
 *
 * 端點設計：
 * - GET /api/v1/materials/{id}        — 查詢單筆物料
 * - GET /api/v1/materials?type=       — 依類型查詢物料列表
 * - GET /api/v1/materials/low-stock   — 查詢低庫存物料
 */
@RestController
@RequestMapping("/api/v1/materials")
public class MaterialQueryController {

    private final MaterialApplicationService applicationService;

    public MaterialQueryController(MaterialApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * 查詢單筆物料。
     *
     * @param id 物料 ID
     * @return 物料檢視
     */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MaterialView>> getMaterial(@PathVariable String id) {
        MaterialView view = applicationService.getMaterial(id);
        return ResponseEntity.ok(ApiResponse.success(view));
    }

    /**
     * 依類型查詢物料列表。
     *
     * @param type 物料類型
     * @return 物料檢視列表
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<MaterialView>>> listMaterials(
            @RequestParam(required = false) String type) {
        List<MaterialView> views = applicationService.listMaterialsByType(type);
        return ResponseEntity.ok(ApiResponse.success(views));
    }

    /**
     * 查詢低庫存物料。
     *
     * @return 低庫存預警檢視列表
     */
    @GetMapping("/low-stock")
    public ResponseEntity<ApiResponse<List<StockAlertView>>> getLowStockMaterials() {
        List<StockAlertView> views = applicationService.getLowStockMaterials();
        return ResponseEntity.ok(ApiResponse.success(views));
    }
}
