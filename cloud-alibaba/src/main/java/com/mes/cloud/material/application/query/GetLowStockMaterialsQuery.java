package com.mes.cloud.material.application.query;

import com.mes.common.cqrs.Query;
import com.mes.cloud.material.application.query.dto.StockAlertView;

import java.util.List;

/**
 * [CQRS Pattern: Query - 查詢低庫存物料]
 * [SOLID: SRP - 只封裝查詢低庫存物料的請求]
 *
 * 此 Query 不需要任何參數，用於取得所有低庫存的物料清單。
 */
public class GetLowStockMaterialsQuery implements Query<List<StockAlertView>> {

    public GetLowStockMaterialsQuery() {
    }
}
