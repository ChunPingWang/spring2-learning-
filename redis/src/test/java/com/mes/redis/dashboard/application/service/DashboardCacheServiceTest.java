package com.mes.redis.dashboard.application.service;

import com.mes.redis.dashboard.application.query.dto.DashboardView;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DashboardCacheService 快取服務測試。
 * 使用 ConcurrentMapCacheManager（非 Redis）測試 Spring Cache 註解行為。
 *
 * 教學重點：
 * Spring Cache 抽象讓我們可以用簡單的記憶體實作測試 @Cacheable/@CacheEvict 行為，
 * 不需要啟動 Redis。
 */
@DisplayName("DashboardCacheService 快取服務測試")
@SpringJUnitConfig(DashboardCacheServiceTest.TestConfig.class)
class DashboardCacheServiceTest {

    @Configuration
    @EnableCaching
    static class TestConfig {

        @Bean
        public CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("dashboard");
        }

        @Bean
        public DashboardCacheService dashboardCacheService() {
            return new DashboardCacheService();
        }
    }

    @Autowired
    private DashboardCacheService cacheService;

    @Autowired
    private CacheManager cacheManager;

    @Test
    @DisplayName("@CachePut 應將資料寫入快取")
    void cachePutShouldStoreInCache() {
        DashboardView view = createTestView("LINE-A");
        cacheService.updateDashboard("LINE-A", view);

        // Spring Cache .get() 回傳 ValueWrapper，需呼叫 .get() 取得實際值
        assertThat(cacheManager.getCache("dashboard").get("LINE-A")).isNotNull();
        assertThat(cacheManager.getCache("dashboard").get("LINE-A").get()).isNotNull();
    }

    @Test
    @DisplayName("@Cacheable 應回傳快取中的值")
    void cacheableShouldReturnCachedValue() {
        DashboardView view = createTestView("LINE-B");
        // 先用 @CachePut 寫入快取
        cacheService.updateDashboard("LINE-B", view);

        // 再用 @Cacheable 取得：應命中快取，回傳快取值（而非方法體的 null）
        DashboardView cached = cacheService.getDashboard("LINE-B");
        assertThat(cached).isNotNull();
        assertThat(cached.getLineId()).isEqualTo("LINE-B");
    }

    @Test
    @DisplayName("@CacheEvict 應清除指定快取")
    void cacheEvictShouldRemoveFromCache() {
        DashboardView view = createTestView("LINE-C");
        cacheService.updateDashboard("LINE-C", view);

        // 驗證快取存在
        assertThat(cacheManager.getCache("dashboard").get("LINE-C")).isNotNull();

        // 清除快取
        cacheService.evictDashboard("LINE-C");

        // 驗證快取已清除
        assertThat(cacheManager.getCache("dashboard").get("LINE-C")).isNull();
    }

    @Test
    @DisplayName("@CacheEvict(allEntries=true) 應清除所有快取")
    void cacheEvictAllShouldClearAllEntries() {
        cacheService.updateDashboard("LINE-D", createTestView("LINE-D"));
        cacheService.updateDashboard("LINE-E", createTestView("LINE-E"));

        // 清除所有
        cacheService.evictAllDashboards();

        // 驗證全部清除
        assertThat(cacheManager.getCache("dashboard").get("LINE-D")).isNull();
        assertThat(cacheManager.getCache("dashboard").get("LINE-E")).isNull();
    }

    private DashboardView createTestView(String lineId) {
        return new DashboardView(
                lineId, 1000, 950, 50,
                new BigDecimal("0.9500"), 120.0,
                new ArrayList<DashboardView.EquipmentStatusView>(),
                LocalDateTime.now());
    }
}
