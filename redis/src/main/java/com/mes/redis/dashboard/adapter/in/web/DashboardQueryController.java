package com.mes.redis.dashboard.adapter.in.web;

import com.mes.common.cqrs.QueryBus;
import com.mes.redis.dashboard.application.query.GetAllLinesOverviewQuery;
import com.mes.redis.dashboard.application.query.GetDashboardQuery;
import com.mes.redis.dashboard.application.query.dto.DashboardView;
import com.mes.redis.dashboard.application.query.dto.LineOverviewView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * [Hexagonal Architecture: Input Adapter - REST Query Controller]
 * [SOLID: SRP - 只負責處理看板的讀取操作（Query）]
 * [SOLID: DIP - 依賴 QueryBus 抽象，不直接依賴 Handler]
 * [CQRS Pattern: Query 端的 HTTP 入口]
 *
 * 看板查詢控制器，處理所有唯讀操作。
 * 與 {@link DashboardCommandController} 分離，體現 CQRS 的讀寫分離原則。
 */
@RestController
@RequestMapping("/api/v1/dashboard")
public class DashboardQueryController {

    private static final Logger log = LoggerFactory.getLogger(DashboardQueryController.class);

    private final QueryBus queryBus;

    public DashboardQueryController(QueryBus queryBus) {
        this.queryBus = queryBus;
    }

    /**
     * 查詢指定產線的看板指標。
     *
     * @param lineId 產線 ID
     * @return 看板指標視圖
     */
    @GetMapping("/{lineId}")
    public ResponseEntity<ApiResponse<DashboardView>> getDashboard(@PathVariable String lineId) {
        log.info("GET /api/v1/dashboard/{} - Querying dashboard", lineId);

        GetDashboardQuery query = new GetDashboardQuery(lineId);
        DashboardView view = queryBus.dispatch(query);
        return ResponseEntity.ok(ApiResponse.success(view));
    }

    /**
     * 查詢所有產線概覽。
     *
     * @return 產線概覽列表
     */
    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<List<LineOverviewView>>> getAllLinesOverview() {
        log.info("GET /api/v1/dashboard/overview - Querying all lines overview");

        GetAllLinesOverviewQuery query = new GetAllLinesOverviewQuery();
        List<LineOverviewView> overview = queryBus.dispatch(query);
        return ResponseEntity.ok(ApiResponse.success(overview));
    }
}
