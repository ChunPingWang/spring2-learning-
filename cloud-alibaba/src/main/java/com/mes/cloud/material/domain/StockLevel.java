package com.mes.cloud.material.domain;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;
import com.mes.common.exception.BusinessRuleViolationException;

import java.util.Arrays;
import java.util.List;

/**
 * [DDD Pattern: Value Object - 庫存水位]
 * [SOLID: SRP - 只負責庫存數量的封裝與計算]
 *
 * 封裝庫存數量與計量單位。不可變物件，所有變更操作都回傳新的 StockLevel。
 *
 * 自我驗證（Self-Validation）：
 * - 數量不可為負數
 * - 扣減時若庫存不足則拋出 BusinessRuleViolationException
 */
@ValueObject
public class StockLevel extends BaseValueObject {

    private final int currentQuantity;
    private final String unit;

    /**
     * 建構 StockLevel。
     *
     * @param currentQuantity 當前庫存數量（不可為負）
     * @param unit            計量單位
     */
    public StockLevel(int currentQuantity, String unit) {
        if (currentQuantity < 0) {
            throw new BusinessRuleViolationException("庫存數量不可為負數，當前值: " + currentQuantity);
        }
        if (unit == null || unit.trim().isEmpty()) {
            throw new BusinessRuleViolationException("計量單位不可為空");
        }
        this.currentQuantity = currentQuantity;
        this.unit = unit;
    }

    /**
     * 增加庫存數量，回傳新的 StockLevel。
     *
     * @param quantity 增加的數量
     * @return 新的 StockLevel
     */
    public StockLevel add(int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleViolationException("增加數量必須為正數，當前值: " + quantity);
        }
        return new StockLevel(this.currentQuantity + quantity, this.unit);
    }

    /**
     * 扣減庫存數量，回傳新的 StockLevel。
     * 若庫存不足會拋出 BusinessRuleViolationException。
     *
     * @param quantity 扣減的數量
     * @return 新的 StockLevel
     */
    public StockLevel subtract(int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleViolationException("扣減數量必須為正數，當前值: " + quantity);
        }
        if (quantity > this.currentQuantity) {
            throw new BusinessRuleViolationException(
                    String.format("庫存不足，當前庫存: %d %s，需要: %d %s",
                            this.currentQuantity, this.unit, quantity, this.unit));
        }
        return new StockLevel(this.currentQuantity - quantity, this.unit);
    }

    /**
     * 判斷庫存是否低於指定最低標準。
     *
     * @param minimum 最低庫存標準
     * @return 若低於最低標準回傳 true
     */
    public boolean isBelow(int minimum) {
        return this.currentQuantity < minimum;
    }

    public int getCurrentQuantity() {
        return currentQuantity;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.asList(currentQuantity, unit);
    }
}
