package com.mes.cloud.material.infrastructure.persistence;

import com.mes.cloud.material.domain.Material;
import com.mes.cloud.material.domain.MaterialId;
import com.mes.cloud.material.domain.MaterialType;
import com.mes.cloud.material.domain.repository.MaterialRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * [DDD Pattern: Repository - Adapter (基礎設施層實作)]
 * [SOLID: DIP - 實作領域層定義的 MaterialRepository 介面]
 * [SOLID: LSP - 可替換為 JPA/MyBatis 等其他實作]
 * [Hexagonal Architecture: Output Adapter]
 *
 * 使用 ConcurrentHashMap 的記憶體實作。
 * 適用於開發、測試和教學環境，不適用於生產環境。
 *
 * 此實作展示了六角形架構的核心概念：
 * 領域層定義介面（Port），基礎設施層提供實作（Adapter）。
 */
@Component
public class InMemoryMaterialRepository implements MaterialRepository {

    private final ConcurrentHashMap<MaterialId, Material> store =
            new ConcurrentHashMap<MaterialId, Material>();

    @Override
    public Optional<Material> findById(MaterialId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Material> findAll() {
        return new ArrayList<Material>(store.values());
    }

    @Override
    public void save(Material aggregate) {
        store.put(aggregate.getId(), aggregate);
    }

    @Override
    public void deleteById(MaterialId id) {
        store.remove(id);
    }

    @Override
    public List<Material> findByType(MaterialType type) {
        List<Material> result = new ArrayList<Material>();
        for (Material material : store.values()) {
            if (material.getMaterialType() == type) {
                result.add(material);
            }
        }
        return result;
    }

    @Override
    public List<Material> findLowStockMaterials() {
        List<Material> result = new ArrayList<Material>();
        for (Material material : store.values()) {
            if (material.isLowStock()) {
                result.add(material);
            }
        }
        return result;
    }

    @Override
    public List<Material> findBySupplier(String supplierId) {
        List<Material> result = new ArrayList<Material>();
        for (Material material : store.values()) {
            if (material.getSupplier().getSupplierId().equals(supplierId)) {
                result.add(material);
            }
        }
        return result;
    }
}
