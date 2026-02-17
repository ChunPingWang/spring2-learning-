package com.mes.cloud.material.application.query;

import com.mes.common.cqrs.Query;
import com.mes.cloud.material.application.query.dto.MaterialView;

/**
 * [CQRS Pattern: Query - 查詢單筆物料]
 * [SOLID: SRP - 只封裝查詢單筆物料所需的參數]
 *
 * Query 是不可變的資料容器，不包含任何業務邏輯。
 * 型別參數 MaterialView 指定了查詢結果的型別。
 */
public class GetMaterialQuery implements Query<MaterialView> {

    private final String materialId;

    public GetMaterialQuery(String materialId) {
        this.materialId = materialId;
    }

    public String getMaterialId() {
        return materialId;
    }
}
