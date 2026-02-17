package com.mes.cloud.material.domain;

import com.mes.common.ddd.annotation.AggregateRoot;
import com.mes.common.ddd.model.BaseAggregateRoot;
import com.mes.common.exception.BusinessRuleViolationException;
import com.mes.common.exception.DomainException;
import com.mes.cloud.material.domain.event.LowStockAlertEvent;
import com.mes.cloud.material.domain.event.MaterialConsumedEvent;
import com.mes.cloud.material.domain.event.MaterialReceivedEvent;

import java.util.Objects;

/**
 * [DDD Pattern: Aggregate Root - 物料聚合根]
 * [SOLID: SRP - 負責物料的庫存管理與業務規則維護]
 * [SOLID: OCP - 可擴展新的業務操作而不修改現有方法]
 *
 * Material 是物料管理的核心聚合根，負責：
 * 1. 管理庫存水位（入庫、消耗、調整）
 * 2. 維護業務規則（庫存不可為負、最低庫存預警）
 * 3. 註冊對應的領域事件
 *
 * 所有庫存變更都必須透過此聚合根的方法進行，
 * 確保業務規則的一致性。
 */
@AggregateRoot
public class Material extends BaseAggregateRoot<MaterialId> {

    private String name;
    private MaterialType materialType;
    private MaterialUnit unit;
    private StockLevel stockLevel;
    private int minimumStock;
    private Supplier supplier;

    /**
     * 建構 Material 聚合根。
     *
     * @param id           物料 ID
     * @param name         物料名稱
     * @param materialType 物料類型
     * @param unit         計量單位
     * @param stockLevel   初始庫存水位
     * @param minimumStock 最低庫存標準
     * @param supplier     供應商
     */
    public Material(MaterialId id, String name, MaterialType materialType,
                    MaterialUnit unit, StockLevel stockLevel,
                    int minimumStock, Supplier supplier) {
        super(id);
        if (name == null || name.trim().isEmpty()) {
            throw new DomainException("物料名稱不可為空");
        }
        this.name = Objects.requireNonNull(name, "物料名稱不可為空");
        this.materialType = Objects.requireNonNull(materialType, "物料類型不可為空");
        this.unit = Objects.requireNonNull(unit, "計量單位不可為空");
        this.stockLevel = Objects.requireNonNull(stockLevel, "庫存水位不可為空");
        if (minimumStock < 0) {
            throw new DomainException("最低庫存標準不可為負數");
        }
        this.minimumStock = minimumStock;
        this.supplier = Objects.requireNonNull(supplier, "供應商不可為空");
    }

    /**
     * 入庫操作：增加庫存數量。
     * 註冊 MaterialReceivedEvent。
     *
     * @param quantity 入庫數量
     */
    public void receive(int quantity) {
        this.stockLevel = this.stockLevel.add(quantity);
        touch();

        registerEvent(new MaterialReceivedEvent(
                getId().getValue(),
                getId().getValue(),
                this.name,
                quantity,
                this.supplier.getSupplierId()
        ));
    }

    /**
     * 消耗操作：扣減庫存數量。
     * 若庫存不足會拋出 BusinessRuleViolationException。
     * 註冊 MaterialConsumedEvent，若低於最低庫存則額外註冊 LowStockAlertEvent。
     *
     * @param quantity    消耗數量
     * @param workOrderId 對應的工單 ID
     */
    public void consume(int quantity, String workOrderId) {
        this.stockLevel = this.stockLevel.subtract(quantity);
        touch();

        registerEvent(new MaterialConsumedEvent(
                getId().getValue(),
                getId().getValue(),
                this.name,
                quantity,
                workOrderId
        ));

        // 檢查是否觸發低庫存預警
        if (isLowStock()) {
            registerEvent(new LowStockAlertEvent(
                    getId().getValue(),
                    getId().getValue(),
                    this.name,
                    this.stockLevel.getCurrentQuantity(),
                    this.minimumStock
            ));
        }
    }

    /**
     * 庫存調整：直接設定庫存數量（例如盤點後的修正）。
     *
     * @param newLevel 新的庫存數量
     */
    public void adjustStock(int newLevel) {
        this.stockLevel = new StockLevel(newLevel, this.stockLevel.getUnit());
        touch();
    }

    /**
     * 判斷庫存是否低於最低標準。
     *
     * @return 若低於最低標準回傳 true
     */
    public boolean isLowStock() {
        return this.stockLevel.isBelow(this.minimumStock);
    }

    /**
     * 更新供應商資訊。
     *
     * @param newSupplier 新的供應商
     */
    public void updateSupplier(Supplier newSupplier) {
        this.supplier = Objects.requireNonNull(newSupplier, "供應商不可為空");
        touch();
    }

    // ========== Getters ==========

    public String getName() {
        return name;
    }

    public MaterialType getMaterialType() {
        return materialType;
    }

    public MaterialUnit getUnit() {
        return unit;
    }

    public StockLevel getStockLevel() {
        return stockLevel;
    }

    public int getMinimumStock() {
        return minimumStock;
    }

    public Supplier getSupplier() {
        return supplier;
    }
}
