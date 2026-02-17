package com.mes.redis.dashboard.application.assembler;

import com.mes.redis.dashboard.application.query.dto.DashboardView;
import com.mes.redis.dashboard.application.query.dto.LineOverviewView;
import com.mes.redis.dashboard.domain.model.DashboardMetrics;
import com.mes.redis.dashboard.domain.model.EquipmentStatusSnapshot;
import com.mes.redis.dashboard.domain.model.ProductionSummary;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * [DDD Pattern: Assembler - 領域物件與 DTO 轉換器]
 * [SOLID: SRP - 只負責領域物件與展示 DTO 之間的轉換]
 *
 * 將領域層的聚合根轉換為應用層的 DTO（View），
 * 解耦領域模型與外部表示。
 */
public final class DashboardAssembler {

    private DashboardAssembler() {
        // 工具類別，禁止實例化
    }

    /**
     * 將 DashboardMetrics 聚合根轉換為 DashboardView DTO。
     *
     * @param metrics 看板指標聚合根
     * @return 看板指標視圖 DTO
     */
    public static DashboardView toView(DashboardMetrics metrics) {
        DashboardView view = new DashboardView();
        view.setLineId(metrics.getLineId());
        view.setLastUpdated(metrics.getSnapshotTime());

        ProductionSummary summary = metrics.getProductionSummary();
        if (summary != null) {
            view.setTotalOutput(summary.getTotalOutput());
            view.setGoodCount(summary.getGoodCount());
            view.setDefectCount(summary.getDefectCount());
            view.setYieldRate(summary.getYieldRate());
            view.setThroughputPerHour(summary.getThroughputPerHour());
        } else {
            view.setYieldRate(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP));
        }

        List<DashboardView.EquipmentStatusView> statusViews = new ArrayList<>();
        for (EquipmentStatusSnapshot snapshot : metrics.getEquipmentStatuses()) {
            statusViews.add(new DashboardView.EquipmentStatusView(
                    snapshot.getEquipmentId(),
                    snapshot.getEquipmentName(),
                    snapshot.getStatus(),
                    snapshot.getLastUpdated()));
        }
        view.setEquipmentStatuses(statusViews);

        return view;
    }

    /**
     * 將多個 DashboardMetrics 轉換為產線概覽列表。
     *
     * @param metricsList 看板指標聚合根列表
     * @return 產線概覽視圖列表
     */
    public static List<LineOverviewView> toOverviewList(List<DashboardMetrics> metricsList) {
        List<LineOverviewView> overviews = new ArrayList<>();
        for (DashboardMetrics metrics : metricsList) {
            overviews.add(toOverview(metrics));
        }
        return overviews;
    }

    private static LineOverviewView toOverview(DashboardMetrics metrics) {
        LineOverviewView overview = new LineOverviewView();
        overview.setLineId(metrics.getLineId());

        ProductionSummary summary = metrics.getProductionSummary();
        if (summary != null) {
            overview.setCurrentOutput(summary.getTotalOutput());
            overview.setYieldRate(summary.getYieldRate());
        } else {
            overview.setYieldRate(BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP));
        }

        int runningCount = 0;
        for (EquipmentStatusSnapshot status : metrics.getEquipmentStatuses()) {
            if ("RUNNING".equals(status.getStatus())) {
                runningCount++;
            }
        }
        overview.setRunningEquipmentCount(runningCount);
        overview.setTotalEquipmentCount(metrics.getEquipmentStatuses().size());

        return overview;
    }
}
