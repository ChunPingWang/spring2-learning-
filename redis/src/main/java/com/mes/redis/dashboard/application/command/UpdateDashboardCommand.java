package com.mes.redis.dashboard.application.command;

import com.mes.common.cqrs.Command;

/**
 * [CQRS Pattern: Command - 更新看板指標]
 * [SOLID: SRP - 只負責攜帶更新看板指標所需的資料]
 *
 * 用於更新某條產線的生產看板摘要數據。
 */
public class UpdateDashboardCommand implements Command {

    private final String lineId;
    private final int totalOutput;
    private final int goodCount;
    private final int defectCount;
    private final double throughputPerHour;

    public UpdateDashboardCommand(String lineId, int totalOutput, int goodCount,
                                  int defectCount, double throughputPerHour) {
        this.lineId = lineId;
        this.totalOutput = totalOutput;
        this.goodCount = goodCount;
        this.defectCount = defectCount;
        this.throughputPerHour = throughputPerHour;
    }

    public String getLineId() {
        return lineId;
    }

    public int getTotalOutput() {
        return totalOutput;
    }

    public int getGoodCount() {
        return goodCount;
    }

    public int getDefectCount() {
        return defectCount;
    }

    public double getThroughputPerHour() {
        return throughputPerHour;
    }
}
