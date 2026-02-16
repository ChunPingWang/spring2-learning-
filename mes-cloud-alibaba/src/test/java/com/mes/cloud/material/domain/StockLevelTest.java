package com.mes.cloud.material.domain;

import com.mes.common.exception.BusinessRuleViolationException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * [DDD Pattern: Value Object 單元測試]
 *
 * 測試 StockLevel Value Object 的：
 * 1. 建構與驗證
 * 2. add 操作
 * 3. subtract 操作
 * 4. isBelow 判斷
 * 5. 值相等性
 */
@DisplayName("StockLevel Value Object 測試")
class StockLevelTest {

    @Nested
    @DisplayName("建構測試")
    class ConstructionTests {

        @Test
        @DisplayName("合法數量應成功建構")
        void shouldCreateWithValidQuantity() {
            StockLevel stockLevel = new StockLevel(100, "KG");
            assertThat(stockLevel.getCurrentQuantity()).isEqualTo(100);
            assertThat(stockLevel.getUnit()).isEqualTo("KG");
        }

        @Test
        @DisplayName("零數量應成功建構")
        void shouldCreateWithZeroQuantity() {
            StockLevel stockLevel = new StockLevel(0, "PCS");
            assertThat(stockLevel.getCurrentQuantity()).isEqualTo(0);
        }

        @Test
        @DisplayName("負數量應拋出 BusinessRuleViolationException")
        void shouldRejectNegativeQuantity() {
            assertThatThrownBy(() -> new StockLevel(-1, "KG"))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("不可為負數");
        }

        @Test
        @DisplayName("空單位應拋出 BusinessRuleViolationException")
        void shouldRejectEmptyUnit() {
            assertThatThrownBy(() -> new StockLevel(10, ""))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("不可為空");
        }
    }

    @Nested
    @DisplayName("add 操作測試")
    class AddTests {

        @Test
        @DisplayName("增加數量應回傳新的 StockLevel")
        void shouldReturnNewStockLevelWithIncreasedQuantity() {
            StockLevel original = new StockLevel(100, "KG");
            StockLevel result = original.add(50);

            assertThat(result.getCurrentQuantity()).isEqualTo(150);
            assertThat(original.getCurrentQuantity()).isEqualTo(100); // 原物件不變
        }

        @Test
        @DisplayName("增加零或負數應拋出例外")
        void shouldRejectNonPositiveAddition() {
            StockLevel stockLevel = new StockLevel(100, "KG");
            assertThatThrownBy(() -> stockLevel.add(0))
                    .isInstanceOf(BusinessRuleViolationException.class);
        }
    }

    @Nested
    @DisplayName("subtract 操作測試")
    class SubtractTests {

        @Test
        @DisplayName("扣減數量應回傳新的 StockLevel")
        void shouldReturnNewStockLevelWithDecreasedQuantity() {
            StockLevel original = new StockLevel(100, "KG");
            StockLevel result = original.subtract(30);

            assertThat(result.getCurrentQuantity()).isEqualTo(70);
            assertThat(original.getCurrentQuantity()).isEqualTo(100); // 原物件不變
        }

        @Test
        @DisplayName("扣減超過庫存應拋出 BusinessRuleViolationException")
        void shouldThrowWhenSubtractExceedsQuantity() {
            StockLevel stockLevel = new StockLevel(10, "KG");
            assertThatThrownBy(() -> stockLevel.subtract(20))
                    .isInstanceOf(BusinessRuleViolationException.class)
                    .hasMessageContaining("庫存不足");
        }

        @Test
        @DisplayName("扣減零或負數應拋出例外")
        void shouldRejectNonPositiveSubtraction() {
            StockLevel stockLevel = new StockLevel(100, "KG");
            assertThatThrownBy(() -> stockLevel.subtract(-5))
                    .isInstanceOf(BusinessRuleViolationException.class);
        }
    }

    @Nested
    @DisplayName("isBelow 判斷測試")
    class IsBelowTests {

        @Test
        @DisplayName("低於最低標準應回傳 true")
        void shouldReturnTrueWhenBelowMinimum() {
            StockLevel stockLevel = new StockLevel(5, "KG");
            assertThat(stockLevel.isBelow(10)).isTrue();
        }

        @Test
        @DisplayName("等於最低標準應回傳 false")
        void shouldReturnFalseWhenEqualToMinimum() {
            StockLevel stockLevel = new StockLevel(10, "KG");
            assertThat(stockLevel.isBelow(10)).isFalse();
        }

        @Test
        @DisplayName("高於最低標準應回傳 false")
        void shouldReturnFalseWhenAboveMinimum() {
            StockLevel stockLevel = new StockLevel(20, "KG");
            assertThat(stockLevel.isBelow(10)).isFalse();
        }
    }

    @Nested
    @DisplayName("值相等性測試")
    class EqualityTests {

        @Test
        @DisplayName("相同數量和單位的 StockLevel 應相等")
        void shouldBeEqualWithSameValues() {
            StockLevel a = new StockLevel(100, "KG");
            StockLevel b = new StockLevel(100, "KG");
            assertThat(a).isEqualTo(b);
            assertThat(a.hashCode()).isEqualTo(b.hashCode());
        }

        @Test
        @DisplayName("不同數量的 StockLevel 應不相等")
        void shouldNotBeEqualWithDifferentQuantity() {
            StockLevel a = new StockLevel(100, "KG");
            StockLevel b = new StockLevel(200, "KG");
            assertThat(a).isNotEqualTo(b);
        }
    }
}
