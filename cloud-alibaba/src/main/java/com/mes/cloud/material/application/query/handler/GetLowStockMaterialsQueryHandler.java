package com.mes.cloud.material.application.query.handler;

import com.mes.common.cqrs.QueryHandler;
import com.mes.cloud.material.application.query.GetLowStockMaterialsQuery;
import com.mes.cloud.material.application.query.dto.StockAlertView;
import com.mes.cloud.material.application.assembler.MaterialAssembler;
import com.mes.cloud.material.domain.Material;
import com.mes.cloud.material.domain.repository.MaterialRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * [CQRS Pattern: Query Handler - 查詢低庫存物料]
 * [SOLID: SRP - 只負責處理 GetLowStockMaterialsQuery]
 * [Hexagonal Architecture: Application Service - 查詢端]
 *
 * 從 Repository 取得所有低庫存物料並轉換為 StockAlertView。
 */
@Component
public class GetLowStockMaterialsQueryHandler
        implements QueryHandler<GetLowStockMaterialsQuery, List<StockAlertView>> {

    private final MaterialRepository repository;

    public GetLowStockMaterialsQueryHandler(MaterialRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<StockAlertView> handle(GetLowStockMaterialsQuery query) {
        List<Material> lowStockMaterials = repository.findLowStockMaterials();
        List<StockAlertView> views = new ArrayList<StockAlertView>();
        for (Material material : lowStockMaterials) {
            views.add(MaterialAssembler.toStockAlertView(material));
        }
        return views;
    }

    @Override
    public Class<GetLowStockMaterialsQuery> getQueryType() {
        return GetLowStockMaterialsQuery.class;
    }
}
