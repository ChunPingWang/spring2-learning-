package com.mes.redis;

import com.mes.redis.dashboard.domain.model.CacheExpiry;
import com.mes.redis.dashboard.domain.model.DashboardMetrics;
import com.mes.redis.dashboard.domain.model.DashboardMetricsId;
import com.mes.redis.dashboard.domain.model.ProductionSummary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * MesRedisApplication 模組驗證測試。
 *
 * 使用 @MockBean 模擬 Redis 連線，避免測試時需要實際的 Redis 伺服器。
 * 排除 Redis 自動配置，防止嘗試建立真實的 Redis 連線。
 * 驗證 Spring 應用上下文可正確載入。
 */
@DisplayName("MesRedisApplication 模組驗證測試")
@SpringBootTest(properties = {
        "spring.cache.type=simple",
        "spring.autoconfigure.exclude=" +
                "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.RedisReactiveAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.RedisRepositoriesAutoConfiguration"
})
class MesRedisApplicationTest {

    @MockBean
    private RedisConnectionFactory redisConnectionFactory;

    @MockBean
    private RedisTemplate<String, Object> redisTemplate;

    @MockBean
    private StringRedisTemplate stringRedisTemplate;

    @Test
    @DisplayName("應用上下文應可正確載入")
    void contextLoads() {
        // 若 Spring 上下文載入失敗，此測試會自動失敗
        assertThat(redisConnectionFactory).isNotNull();
    }

    @Test
    @DisplayName("主程式類別應可被載入")
    void applicationClassShouldBeLoadable() {
        assertThat(MesRedisApplication.class).isNotNull();
        assertThat(MesRedisApplication.class.getAnnotations()).isNotEmpty();
    }

    @Test
    @DisplayName("核心領域物件應可正確建立")
    void coreDomainObjectsShouldBeCreatable() {
        DashboardMetricsId id = DashboardMetricsId.of("LINE-A", "20240101");
        CacheExpiry expiry = new CacheExpiry(1800, LocalDateTime.now());
        DashboardMetrics metrics = new DashboardMetrics(id, "LINE-A", expiry);

        assertThat(metrics.getId().getValue()).isEqualTo("LINE-A:20240101");
        assertThat(metrics.getLineId()).isEqualTo("LINE-A");

        ProductionSummary summary = new ProductionSummary(1000, 950, 50, 120.0);
        metrics.updateProductionSummary(summary);

        assertThat(metrics.getProductionSummary().getTotalOutput()).isEqualTo(1000);
        assertThat(metrics.getProductionSummary().getYieldRate().doubleValue()).isEqualTo(0.95);
    }
}
