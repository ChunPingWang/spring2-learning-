package com.mes.redis.dashboard.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ProductionSummary 值物件單元測試。
 * 驗證建立、良率計算、getter、相等性等邏輯。
 */
@DisplayName("ProductionSummary 值物件測試")
class ProductionSummaryTest {

    @Nested
    @DisplayName("建立測試")
    class CreationTest {

        @Test
        @DisplayName("應可正確建立生產摘要")
        void shouldCreateProductionSummary() {
            ProductionSummary summary = new ProductionSummary(1000, 950, 50, 120.5);

            assertThat(summary.getTotalOutput()).isEqualTo(1000);
            assertThat(summary.getGoodCount()).isEqualTo(950);
            assertThat(summary.getDefectCount()).isEqualTo(50);
            assertThat(summary.getThroughputPerHour()).isEqualTo(120.5);
        }

        @Test
        @DisplayName("總產出不可為負數")
        void shouldRejectNegativeTotalOutput() {
            assertThatThrownBy(() -> new ProductionSummary(-1, 0, 0, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Total output must not be negative");
        }

        @Test
        @DisplayName("良品數不可為負數")
        void shouldRejectNegativeGoodCount() {
            assertThatThrownBy(() -> new ProductionSummary(100, -1, 0, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Good count must not be negative");
        }

        @Test
        @DisplayName("不良品數不可為負數")
        void shouldRejectNegativeDefectCount() {
            assertThatThrownBy(() -> new ProductionSummary(100, 100, -1, 0))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Defect count must not be negative");
        }

        @Test
        @DisplayName("每小時產量不可為負數")
        void shouldRejectNegativeThroughput() {
            assertThatThrownBy(() -> new ProductionSummary(100, 100, 0, -1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("Throughput per hour must not be negative");
        }
    }

    @Nested
    @DisplayName("良率計算測試")
    class YieldRateTest {

        @Test
        @DisplayName("正常情況下應正確計算良率")
        void shouldCalculateYieldRateCorrectly() {
            ProductionSummary summary = new ProductionSummary(1000, 950, 50, 120.0);
            BigDecimal expected = new BigDecimal("0.9500");

            assertThat(summary.getYieldRate()).isEqualByComparingTo(expected);
            assertThat(summary.getYieldRate().scale()).isEqualTo(4);
        }

        @Test
        @DisplayName("總產出為 0 時良率應為 0")
        void shouldReturnZeroYieldRateWhenTotalOutputIsZero() {
            ProductionSummary summary = new ProductionSummary(0, 0, 0, 0);

            assertThat(summary.getYieldRate()).isEqualByComparingTo(BigDecimal.ZERO);
            assertThat(summary.getYieldRate().scale()).isEqualTo(4);
        }

        @Test
        @DisplayName("全部良品時良率應為 1.0000")
        void shouldReturnOneWhenAllGood() {
            ProductionSummary summary = new ProductionSummary(500, 500, 0, 100.0);
            BigDecimal expected = BigDecimal.ONE.setScale(4, RoundingMode.HALF_UP);

            assertThat(summary.getYieldRate()).isEqualByComparingTo(expected);
        }
    }

    @Nested
    @DisplayName("相等性測試")
    class EqualityTest {

        @Test
        @DisplayName("相同屬性的摘要應相等")
        void shouldBeEqualWithSameValues() {
            ProductionSummary s1 = new ProductionSummary(1000, 950, 50, 120.0);
            ProductionSummary s2 = new ProductionSummary(1000, 950, 50, 120.0);

            assertThat(s1).isEqualTo(s2);
            assertThat(s1.hashCode()).isEqualTo(s2.hashCode());
        }

        @Test
        @DisplayName("不同屬性的摘要應不相等")
        void shouldNotBeEqualWithDifferentValues() {
            ProductionSummary s1 = new ProductionSummary(1000, 950, 50, 120.0);
            ProductionSummary s2 = new ProductionSummary(1000, 900, 100, 120.0);

            assertThat(s1).isNotEqualTo(s2);
        }
    }
}
