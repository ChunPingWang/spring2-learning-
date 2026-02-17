package com.mes.redis.dashboard.domain.repository;

import com.mes.common.ddd.repository.Repository;
import com.mes.redis.dashboard.domain.model.DashboardMetrics;
import com.mes.redis.dashboard.domain.model.DashboardMetricsId;

import java.util.List;
import java.util.Optional;

/**
 * [DDD Pattern: Repository - Port (出站埠)]
 * [SOLID: ISP - 定義看板指標特有的查詢方法]
 * [SOLID: DIP - 領域層定義介面，基礎設施層實作]
 * [Hexagonal Architecture: Output Port]
 *
 * 看板指標 Repository 介面，定義在領域層中。
 * 除基本 CRUD 外，提供依產線 ID 查詢的方法。
 */
public interface DashboardMetricsRepository extends Repository<DashboardMetrics, DashboardMetricsId> {

    /**
     * 依產線 ID 查詢所有看板指標。
     *
     * @param lineId 產線 ID
     * @return 該產線的所有看板指標
     */
    List<DashboardMetrics> findByLineId(String lineId);

    /**
     * 依產線 ID 查詢最新的看板指標。
     *
     * @param lineId 產線 ID
     * @return 最新的看板指標（如果存在）
     */
    Optional<DashboardMetrics> findLatestByLineId(String lineId);
}
