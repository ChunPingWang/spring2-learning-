package com.mes.redis.dashboard.infrastructure.cache;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mes.redis.dashboard.application.query.dto.DashboardView;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * RedisCacheAdapter 快取配接器測試。
 * 使用 Mock RedisTemplate 驗證 Redis 操作呼叫。
 */
@DisplayName("RedisCacheAdapter 快取配接器測試")
class RedisCacheAdapterTest {

    @SuppressWarnings("unchecked")
    private RedisTemplate<String, Object> redisTemplate = mock(RedisTemplate.class);

    @SuppressWarnings("unchecked")
    private ValueOperations<String, Object> valueOps = mock(ValueOperations.class);

    private ObjectMapper objectMapper;
    private RedisCacheAdapter adapter;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        when(redisTemplate.opsForValue()).thenReturn(valueOps);
        adapter = new RedisCacheAdapter(redisTemplate, objectMapper);
    }

    @Test
    @DisplayName("put 應呼叫 opsForValue().set() 並設定 TTL")
    void putShouldCallOpsForValueSet() {
        DashboardView view = createTestView("LINE-A");

        adapter.put("dashboard:LINE-A", view, 1800);

        verify(valueOps).set(eq("dashboard:LINE-A"), eq(view), eq(1800L), eq(TimeUnit.SECONDS));
    }

    @Test
    @DisplayName("get 命中時應回傳快取值")
    void getShouldReturnCachedValue() {
        DashboardView cachedView = createTestView("LINE-A");
        when(valueOps.get("dashboard:LINE-A")).thenReturn(cachedView);

        DashboardView result = adapter.get("dashboard:LINE-A", DashboardView.class);

        assertThat(result).isNotNull();
        assertThat(result.getLineId()).isEqualTo("LINE-A");
    }

    @Test
    @DisplayName("get 未命中時應回傳 null")
    void getShouldReturnNullOnMiss() {
        when(valueOps.get("dashboard:LINE-X")).thenReturn(null);

        DashboardView result = adapter.get("dashboard:LINE-X", DashboardView.class);

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("evict 應呼叫 redisTemplate.delete()")
    void evictShouldCallDelete() {
        adapter.evict("dashboard:LINE-A");

        verify(redisTemplate).delete("dashboard:LINE-A");
    }

    @Test
    @DisplayName("evictByPattern 應呼叫 keys() 與 delete()")
    void evictByPatternShouldCallKeysAndDelete() {
        HashSet<String> keys = new HashSet<>(Arrays.asList("dashboard:LINE-A", "dashboard:LINE-B"));
        when(redisTemplate.keys("dashboard:*")).thenReturn(keys);

        adapter.evictByPattern("dashboard:*");

        verify(redisTemplate).keys("dashboard:*");
        verify(redisTemplate).delete(keys);
    }

    @Test
    @DisplayName("exists 應呼叫 hasKey()")
    void existsShouldCallHasKey() {
        when(redisTemplate.hasKey("dashboard:LINE-A")).thenReturn(Boolean.TRUE);

        boolean result = adapter.exists("dashboard:LINE-A");

        assertThat(result).isTrue();
        verify(redisTemplate).hasKey("dashboard:LINE-A");
    }

    private DashboardView createTestView(String lineId) {
        return new DashboardView(
                lineId, 1000, 950, 50,
                new BigDecimal("0.9500"), 120.0,
                new ArrayList<DashboardView.EquipmentStatusView>(),
                LocalDateTime.now());
    }
}
