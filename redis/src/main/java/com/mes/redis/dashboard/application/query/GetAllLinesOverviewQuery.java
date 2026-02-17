package com.mes.redis.dashboard.application.query;

import com.mes.common.cqrs.Query;
import com.mes.redis.dashboard.application.query.dto.LineOverviewView;

import java.util.List;

/**
 * [CQRS Pattern: Query - 查詢所有產線概覽]
 * [SOLID: SRP - 只負責表達「取得所有產線概覽」的查詢意圖]
 *
 * 用於查詢所有產線的概覽數據（總產出、良率、設備狀態等）。
 */
public class GetAllLinesOverviewQuery implements Query<List<LineOverviewView>> {

    public GetAllLinesOverviewQuery() {
    }
}
