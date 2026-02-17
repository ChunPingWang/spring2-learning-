package com.mes.cloud.material.domain;

import com.mes.common.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [DDD Pattern: Value Object 單元測試]
 *
 * 測試 Supplier Value Object 的建構、驗證與值相等性。
 */
@DisplayName("Supplier Value Object 測試")
class SupplierTest {

    @Test
    @DisplayName("合法參數應成功建構")
    void shouldCreateWithValidParameters() {
        Supplier supplier = new Supplier("SUP-001", "台灣鋼鐵", "02-12345678");

        assertThat(supplier.getSupplierId()).isEqualTo("SUP-001");
        assertThat(supplier.getSupplierName()).isEqualTo("台灣鋼鐵");
        assertThat(supplier.getContactInfo()).isEqualTo("02-12345678");
    }

    @Test
    @DisplayName("供應商 ID 不可為空")
    void shouldRejectNullSupplierId() {
        assertThatThrownBy(() -> new Supplier(null, "供應商", "info"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("供應商 ID 不可為空");
    }

    @Test
    @DisplayName("供應商名稱不可為空")
    void shouldRejectNullSupplierName() {
        assertThatThrownBy(() -> new Supplier("SUP-001", null, "info"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("供應商名稱不可為空");
    }

    @Test
    @DisplayName("相同值的供應商應相等")
    void shouldBeEqualWithSameValues() {
        Supplier a = new Supplier("SUP-001", "台灣鋼鐵", "02-12345678");
        Supplier b = new Supplier("SUP-001", "台灣鋼鐵", "02-12345678");

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
