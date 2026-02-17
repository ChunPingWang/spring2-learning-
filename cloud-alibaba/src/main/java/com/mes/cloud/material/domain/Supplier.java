package com.mes.cloud.material.domain;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;
import com.mes.common.exception.BusinessRuleViolationException;

import java.util.Arrays;
import java.util.List;

/**
 * [DDD Pattern: Value Object - 供應商資訊]
 * [SOLID: SRP - 只負責供應商資訊的封裝]
 *
 * 不可變物件，封裝供應商的基本資訊。
 * 作為 Material 聚合根的內嵌 Value Object。
 */
@ValueObject
public class Supplier extends BaseValueObject {

    private final String supplierId;
    private final String supplierName;
    private final String contactInfo;

    /**
     * 建構 Supplier。
     *
     * @param supplierId   供應商 ID
     * @param supplierName 供應商名稱
     * @param contactInfo  聯絡資訊
     */
    public Supplier(String supplierId, String supplierName, String contactInfo) {
        if (supplierId == null || supplierId.trim().isEmpty()) {
            throw new BusinessRuleViolationException("供應商 ID 不可為空");
        }
        if (supplierName == null || supplierName.trim().isEmpty()) {
            throw new BusinessRuleViolationException("供應商名稱不可為空");
        }
        this.supplierId = supplierId;
        this.supplierName = supplierName;
        this.contactInfo = contactInfo;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public String getContactInfo() {
        return contactInfo;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.asList(supplierId, supplierName, contactInfo);
    }
}
