package com.mes.redis.dashboard.application.query;

import com.mes.common.cqrs.Query;
import com.mes.redis.dashboard.application.query.dto.DashboardView;

/**
 * [CQRS Pattern: Query - 查詢看板指標]
 * [SOLID: SRP - 只負責攜帶查詢看板指標所需的產線 ID]
 *
 * 用於查詢指定產線的看板指標數據。
 */
public class GetDashboardQuery implements Query<DashboardView> {

    private final String lineId;

    public GetDashboardQuery(String lineId) {
        this.lineId = lineId;
    }

    public String getLineId() {
        return lineId;
    }
}
