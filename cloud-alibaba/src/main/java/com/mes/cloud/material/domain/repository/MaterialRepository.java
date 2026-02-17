package com.mes.cloud.material.domain.repository;

import com.mes.common.ddd.repository.Repository;
import com.mes.cloud.material.domain.Material;
import com.mes.cloud.material.domain.MaterialId;
import com.mes.cloud.material.domain.MaterialType;

import java.util.List;

/**
 * [DDD Pattern: Repository - Port (出站埠)]
 * [SOLID: ISP - 定義物料聚合根專屬的查詢方法]
 * [SOLID: DIP - 領域層定義介面，基礎設施層實作]
 * [Hexagonal Architecture: Output Port]
 *
 * 物料的 Repository 介面，擴展通用 Repository 並新增特定查詢方法。
 * 此介面定義在領域層，實作放在基礎設施層，實現依賴反轉。
 */
public interface MaterialRepository extends Repository<Material, MaterialId> {

    /**
     * 依物料類型查找所有物料。
     *
     * @param type 物料類型
     * @return 該類型的所有物料
     */
    List<Material> findByType(MaterialType type);

    /**
     * 查找所有低庫存的物料。
     *
     * @return 庫存低於最低標準的所有物料
     */
    List<Material> findLowStockMaterials();

    /**
     * 依供應商 ID 查找所有物料。
     *
     * @param supplierId 供應商 ID
     * @return 該供應商提供的所有物料
     */
    List<Material> findBySupplier(String supplierId);
}
