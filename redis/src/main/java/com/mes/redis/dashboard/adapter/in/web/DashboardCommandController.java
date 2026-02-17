package com.mes.redis.dashboard.adapter.in.web;

import com.mes.common.cqrs.CommandBus;
import com.mes.redis.dashboard.application.command.InvalidateCacheCommand;
import com.mes.redis.dashboard.application.command.UpdateDashboardCommand;
import com.mes.redis.dashboard.application.command.UpdateEquipmentStatusCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * [Hexagonal Architecture: Input Adapter - REST Command Controller]
 * [SOLID: SRP - 只負責處理看板的寫入操作（Command）]
 * [SOLID: DIP - 依賴 CommandBus 抽象，不直接依賴 Handler]
 * [CQRS Pattern: Command 端的 HTTP 入口]
 *
 * 看板命令控制器，處理所有寫入操作。
 * 與 {@link DashboardQueryController} 分離，體現 CQRS 的讀寫分離原則。
 */
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardCommandController {

    private static final Logger log = LoggerFactory.getLogger(DashboardCommandController.class);

    private final CommandBus commandBus;

    public DashboardCommandController(CommandBus commandBus) {
        this.commandBus = commandBus;
    }

    /**
     * 更新看板指標。
     *
     * @param request 包含 lineId, totalOutput, goodCount, defectCount, throughputPerHour 的請求
     * @return 操作結果
     */
    @PostMapping("/update")
    public ResponseEntity<ApiResponse<Void>> updateDashboard(@RequestBody Map<String, Object> request) {
        log.info("POST /api/v1/dashboard/update - Updating dashboard");

        UpdateDashboardCommand command = new UpdateDashboardCommand(
                (String) request.get("lineId"),
                toInt(request.get("totalOutput")),
                toInt(request.get("goodCount")),
                toInt(request.get("defectCount")),
                toDouble(request.get("throughputPerHour")));

        commandBus.dispatch(command);
        return ResponseEntity.ok(ApiResponse.<Void>success("Dashboard updated", null));
    }

    /**
     * 更新設備狀態。
     *
     * @param lineId  產線 ID
     * @param request 包含 equipmentId, equipmentName, status 的請求
     * @return 操作結果
     */
    @PutMapping("/{lineId}/equipment")
    public ResponseEntity<ApiResponse<Void>> updateEquipmentStatus(
            @PathVariable String lineId, @RequestBody Map<String, String> request) {
        log.info("PUT /api/v1/dashboard/{}/equipment - Updating equipment status", lineId);

        UpdateEquipmentStatusCommand command = new UpdateEquipmentStatusCommand(
                lineId,
                request.get("equipmentId"),
                request.get("equipmentName"),
                request.get("status"));

        commandBus.dispatch(command);
        return ResponseEntity.ok(ApiResponse.<Void>success("Equipment status updated", null));
    }

    /**
     * 手動清除快取。
     *
     * @param key 快取鍵
     * @return 操作結果
     */
    @DeleteMapping("/cache/{key}")
    public ResponseEntity<ApiResponse<Void>> invalidateCache(@PathVariable String key) {
        log.info("DELETE /api/v1/dashboard/cache/{} - Invalidating cache", key);

        InvalidateCacheCommand command = new InvalidateCacheCommand(key);
        commandBus.dispatch(command);
        return ResponseEntity.ok(ApiResponse.<Void>success("Cache invalidated", null));
    }

    private int toInt(Object value) {
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        return Integer.parseInt(String.valueOf(value));
    }

    private double toDouble(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        return Double.parseDouble(String.valueOf(value));
    }
}
