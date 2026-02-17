package com.mes.redis.dashboard.domain.model;

import com.mes.common.ddd.annotation.ValueObject;
import com.mes.common.ddd.model.BaseValueObject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.List;

/**
 * [DDD Pattern: Value Object - 生產摘要]
 * [SOLID: SRP - 只負責封裝生產產出的統計資訊]
 *
 * 不可變的生產摘要值物件，包含：
 * - 總產出數量
 * - 良品數量
 * - 不良品數量
 * - 每小時產量
 *
 * 提供計算良率 (Yield Rate) 的方法。
 */
@ValueObject
public class ProductionSummary extends BaseValueObject {

    private final int totalOutput;
    private final int goodCount;
    private final int defectCount;
    private final double throughputPerHour;

    public ProductionSummary(int totalOutput, int goodCount, int defectCount, double throughputPerHour) {
        if (totalOutput < 0) {
            throw new IllegalArgumentException("Total output must not be negative");
        }
        if (goodCount < 0) {
            throw new IllegalArgumentException("Good count must not be negative");
        }
        if (defectCount < 0) {
            throw new IllegalArgumentException("Defect count must not be negative");
        }
        if (throughputPerHour < 0) {
            throw new IllegalArgumentException("Throughput per hour must not be negative");
        }
        this.totalOutput = totalOutput;
        this.goodCount = goodCount;
        this.defectCount = defectCount;
        this.throughputPerHour = throughputPerHour;
    }

    /**
     * 計算良率（良品數 / 總產出），回傳 BigDecimal（scale=4）。
     * 若總產出為 0，回傳 BigDecimal.ZERO。
     *
     * @return 良率，例如 0.9500 表示 95%
     */
    public BigDecimal getYieldRate() {
        if (totalOutput == 0) {
            return BigDecimal.ZERO.setScale(4, RoundingMode.HALF_UP);
        }
        return BigDecimal.valueOf(goodCount)
                .divide(BigDecimal.valueOf(totalOutput), 4, RoundingMode.HALF_UP);
    }

    public int getTotalOutput() {
        return totalOutput;
    }

    public int getGoodCount() {
        return goodCount;
    }

    public int getDefectCount() {
        return defectCount;
    }

    public double getThroughputPerHour() {
        return throughputPerHour;
    }

    @Override
    protected List<Object> getEqualityComponents() {
        return Arrays.asList(totalOutput, goodCount, defectCount, throughputPerHour);
    }
}
