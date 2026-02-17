package com.mes.cloud.material.domain;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;
import com.mes.common.exception.BusinessRuleViolationException;

import java.util.Arrays;
import java.util.List;

/**
 * [DDD Pattern: Value Object - 物料計量單位]
 * [SOLID: SRP - 只負責計量單位的封裝]
 *
 * 不可變物件，封裝物料的計量單位（如公斤、個、卷等）。
 * 提供預定義的常用單位作為靜態常數。
 */
@ValueObject
public class MaterialUnit extends BaseValueObject {

    /** 公斤 */
    public static final MaterialUnit KG = new MaterialUnit("KG", "公斤");

    /** 個 / 件 */
    public static final MaterialUnit PCS = new MaterialUnit("PCS", "個");

    /** 卷 */
    public static final MaterialUnit ROLL = new MaterialUnit("ROLL", "卷");

    /** 公尺 */
    public static final MaterialUnit METER = new MaterialUnit("METER", "公尺");

    /** 公升 */
    public static final MaterialUnit LITER = new MaterialUnit("LITER", "公升");

    private final String unitCode;
    private final String unitName;

    /**
     * 建構 MaterialUnit。
     *
     * @param unitCode 單位代碼
     * @param unitName 單位名稱
     */
    public MaterialUnit(String unitCode, String unitName) {
        if (unitCode == null || unitCode.trim().isEmpty()) {
            throw new BusinessRuleViolationException("單位代碼不可為空");
        }
        if (unitName == null || unitName.trim().isEmpty()) {
            throw new BusinessRuleViolationException("單位名稱不可為空");
        }
        this.unitCode = unitCode;
        this.unitName = unitName;
    }

    public String getUnitCode() {
        return unitCode;
    }

    public String getUnitName() {
        return unitName;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.asList(unitCode, unitName);
    }
}
