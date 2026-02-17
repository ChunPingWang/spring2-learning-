package com.mes.cloud.material.application.query.handler;

import com.mes.common.cqrs.QueryHandler;
import com.mes.cloud.material.application.assembler.MaterialAssembler;
import com.mes.cloud.material.application.query.ListMaterialsByTypeQuery;
import com.mes.cloud.material.application.query.dto.MaterialView;
import com.mes.cloud.material.domain.Material;
import com.mes.cloud.material.domain.MaterialType;
import com.mes.cloud.material.domain.repository.MaterialRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * [CQRS Pattern: Query Handler - 依類型查詢物料列表]
 * [SOLID: SRP - 只負責處理 ListMaterialsByTypeQuery]
 * [Hexagonal Architecture: Application Service - 查詢端]
 *
 * 將字串型態的 materialType 轉換為列舉後查詢 Repository。
 */
@Component
public class ListMaterialsByTypeQueryHandler
        implements QueryHandler<ListMaterialsByTypeQuery, List<MaterialView>> {

    private final MaterialRepository repository;

    public ListMaterialsByTypeQueryHandler(MaterialRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MaterialView> handle(ListMaterialsByTypeQuery query) {
        MaterialType type = MaterialType.valueOf(query.getMaterialType());
        List<Material> materials = repository.findByType(type);
        return MaterialAssembler.toViewList(materials);
    }

    @Override
    public Class<ListMaterialsByTypeQuery> getQueryType() {
        return ListMaterialsByTypeQuery.class;
    }
}
