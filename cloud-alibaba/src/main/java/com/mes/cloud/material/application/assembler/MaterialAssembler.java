package com.mes.cloud.material.application.assembler;

import com.mes.cloud.material.application.query.dto.MaterialView;
import com.mes.cloud.material.application.query.dto.StockAlertView;
import com.mes.cloud.material.domain.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * [DDD Pattern: Assembler - 領域物件與 DTO 的轉換]
 * [SOLID: SRP - 只負責 Material 與 View DTO 之間的映射]
 *
 * Assembler 負責在不同層的物件之間進行轉換。
 * 在 CQRS 中，將 Domain Model (寫入模型) 轉換為 Read Model (讀取模型)。
 *
 * 使用靜態方法以保持無狀態，避免不必要的物件建立。
 */
public final class MaterialAssembler {

    private MaterialAssembler() {
        // 工具類別，禁止實例化
    }

    /**
     * 將 Material 聚合根轉換為 MaterialView DTO。
     *
     * @param material 物料聚合根
     * @return 物料檢視 DTO
     */
    public static MaterialView toView(Material material) {
        if (material == null) {
            return null;
        }

        MaterialView view = new MaterialView();
        view.setId(material.getId().getValue());
        view.setName(material.getName());
        view.setType(material.getMaterialType().name());
        view.setTypeName(material.getMaterialType().getDescription());
        view.setStockQuantity(material.getStockLevel().getCurrentQuantity());
        view.setUnit(material.getUnit().getUnitName());
        view.setSupplierName(material.getSupplier().getSupplierName());
        view.setLowStock(material.isLowStock());
        view.setCreatedAt(material.getCreatedAt());

        return view;
    }

    /**
     * 將多筆 Material 轉換為 MaterialView 列表。
     *
     * @param materials 物料列表
     * @return 物料檢視列表
     */
    public static List<MaterialView> toViewList(List<Material> materials) {
        List<MaterialView> views = new ArrayList<MaterialView>();
        if (materials != null) {
            for (Material material : materials) {
                views.add(toView(material));
            }
        }
        return views;
    }

    /**
     * 將 Material 聚合根轉換為 StockAlertView DTO。
     *
     * @param material 物料聚合根
     * @return 低庫存預警檢視 DTO
     */
    public static StockAlertView toStockAlertView(Material material) {
        if (material == null) {
            return null;
        }

        return new StockAlertView(
                material.getId().getValue(),
                material.getName(),
                material.getStockLevel().getCurrentQuantity(),
                material.getMinimumStock()
        );
    }
}
