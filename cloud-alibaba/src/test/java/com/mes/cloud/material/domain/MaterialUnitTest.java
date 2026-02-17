package com.mes.cloud.material.domain;

import com.mes.common.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [DDD Pattern: Value Object 單元測試]
 *
 * 測試 MaterialUnit Value Object 的建構、驗證、預定義單位與值相等性。
 */
@DisplayName("MaterialUnit Value Object 測試")
class MaterialUnitTest {

    @Test
    @DisplayName("合法參數應成功建構")
    void shouldCreateWithValidParameters() {
        MaterialUnit unit = new MaterialUnit("BOX", "箱");
        assertThat(unit.getUnitCode()).isEqualTo("BOX");
        assertThat(unit.getUnitName()).isEqualTo("箱");
    }

    @Test
    @DisplayName("預定義單位應有正確的代碼和名稱")
    void shouldHaveCorrectPredefinedUnits() {
        assertThat(MaterialUnit.KG.getUnitCode()).isEqualTo("KG");
        assertThat(MaterialUnit.KG.getUnitName()).isEqualTo("公斤");
        assertThat(MaterialUnit.PCS.getUnitCode()).isEqualTo("PCS");
        assertThat(MaterialUnit.PCS.getUnitName()).isEqualTo("個");
        assertThat(MaterialUnit.ROLL.getUnitCode()).isEqualTo("ROLL");
        assertThat(MaterialUnit.METER.getUnitCode()).isEqualTo("METER");
        assertThat(MaterialUnit.LITER.getUnitCode()).isEqualTo("LITER");
    }

    @Test
    @DisplayName("相同代碼和名稱的單位應相等")
    void shouldBeEqualWithSameValues() {
        MaterialUnit a = new MaterialUnit("KG", "公斤");
        MaterialUnit b = new MaterialUnit("KG", "公斤");
        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    @DisplayName("空單位代碼應拋出 BusinessRuleViolationException")
    void shouldRejectEmptyUnitCode() {
        assertThatThrownBy(() -> new MaterialUnit("", "公斤"))
                .isInstanceOf(BusinessRuleViolationException.class)
                .hasMessageContaining("單位代碼不可為空");
    }
}
