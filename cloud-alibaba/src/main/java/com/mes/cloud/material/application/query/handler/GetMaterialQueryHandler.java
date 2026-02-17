package com.mes.cloud.material.application.query.handler;

import com.mes.common.cqrs.QueryHandler;
import com.mes.common.exception.EntityNotFoundException;
import com.mes.cloud.material.application.assembler.MaterialAssembler;
import com.mes.cloud.material.application.query.GetMaterialQuery;
import com.mes.cloud.material.application.query.dto.MaterialView;
import com.mes.cloud.material.domain.Material;
import com.mes.cloud.material.domain.MaterialId;
import com.mes.cloud.material.domain.repository.MaterialRepository;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Query Handler - 查詢單筆物料]
 * [SOLID: SRP - 只負責處理 GetMaterialQuery]
 * [Hexagonal Architecture: Application Service - 查詢端]
 *
 * 透過 Repository 載入物料後用 Assembler 轉換為 DTO。
 */
@Component
public class GetMaterialQueryHandler
        implements QueryHandler<GetMaterialQuery, MaterialView> {

    private final MaterialRepository repository;

    public GetMaterialQueryHandler(MaterialRepository repository) {
        this.repository = repository;
    }

    @Override
    public MaterialView handle(GetMaterialQuery query) {
        MaterialId materialId = MaterialId.of(query.getMaterialId());
        Material material = repository.findById(materialId)
                .orElseThrow(() -> new EntityNotFoundException("Material", query.getMaterialId()));

        return MaterialAssembler.toView(material);
    }

    @Override
    public Class<GetMaterialQuery> getQueryType() {
        return GetMaterialQuery.class;
    }
}
