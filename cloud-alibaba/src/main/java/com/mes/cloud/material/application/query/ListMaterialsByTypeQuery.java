package com.mes.cloud.material.application.query;

import com.mes.common.cqrs.Query;
import com.mes.cloud.material.application.query.dto.MaterialView;

import java.util.List;

/**
 * [CQRS Pattern: Query - 依類型查詢物料列表]
 * [SOLID: SRP - 只封裝依類型查詢物料所需的參數]
 */
public class ListMaterialsByTypeQuery implements Query<List<MaterialView>> {

    private final String materialType;

    public ListMaterialsByTypeQuery(String materialType) {
        this.materialType = materialType;
    }

    public String getMaterialType() {
        return materialType;
    }
}
