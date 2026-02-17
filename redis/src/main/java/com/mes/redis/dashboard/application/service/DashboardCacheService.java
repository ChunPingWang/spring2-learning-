package com.mes.redis.dashboard.application.service;

import com.mes.redis.dashboard.application.query.dto.DashboardView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

/**
 * [DDD Pattern: Application Service - 看板快取服務]
 * [SOLID: SRP - 只負責看板快取的 Spring Cache 抽象操作]
 *
 * 教學重點：Spring Cache 抽象
 * <ul>
 *   <li>{@code @Cacheable} - 方法結果會被快取，同一參數的後續呼叫直接回傳快取值</li>
 *   <li>{@code @CachePut} - 每次都執行方法，但結果會更新快取</li>
 *   <li>{@code @CacheEvict} - 清除指定快取</li>
 *   <li>{@code @CacheEvict(allEntries = true)} - 清除整個快取區域</li>
 * </ul>
 *
 * Spring Cache 抽象讓切換快取實作（ConcurrentMap、EhCache、Redis）只需改設定，
 * 不需改程式碼。在本模組中，底層使用 Redis 作為快取儲存。
 */
@Service
public class DashboardCacheService {

    private static final Logger log = LoggerFactory.getLogger(DashboardCacheService.class);

    /**
     * 取得看板指標（帶快取）。
     * Spring 會先檢查 "dashboard" 快取中是否有 key = lineId 的值，
     * 若有則直接回傳，不執行此方法。
     *
     * @param lineId 產線 ID
     * @return 看板指標視圖（此處回傳 null 表示快取中無值時的預設行為）
     */
    @Cacheable(value = "dashboard", key = "#lineId")
    public DashboardView getDashboard(String lineId) {
        log.debug("@Cacheable MISS - getDashboard called for lineId={}", lineId);
        // 實際場景中，此處會從 Repository 載入
        // 回傳 null 表示快取未命中且無值
        return null;
    }

    /**
     * 更新看板快取。
     * 無論快取中是否已有值，都會執行此方法並更新快取。
     *
     * @param lineId 產線 ID
     * @param view   看板指標視圖
     * @return 看板指標視圖
     */
    @CachePut(value = "dashboard", key = "#lineId")
    public DashboardView updateDashboard(String lineId, DashboardView view) {
        log.debug("@CachePut - updateDashboard called for lineId={}", lineId);
        return view;
    }

    /**
     * 清除指定產線的看板快取。
     *
     * @param lineId 產線 ID
     */
    @CacheEvict(value = "dashboard", key = "#lineId")
    public void evictDashboard(String lineId) {
        log.debug("@CacheEvict - evictDashboard called for lineId={}", lineId);
    }

    /**
     * 清除所有看板快取。
     */
    @CacheEvict(value = "dashboard", allEntries = true)
    public void evictAllDashboards() {
        log.debug("@CacheEvict(allEntries=true) - evictAllDashboards called");
    }
}
