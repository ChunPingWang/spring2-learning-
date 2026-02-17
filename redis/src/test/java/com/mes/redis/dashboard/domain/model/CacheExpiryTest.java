package com.mes.redis.dashboard.domain.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * CacheExpiry 值物件單元測試。
 * 驗證過期判斷邏輯、TTL 邊界。
 */
@DisplayName("CacheExpiry 值物件測試")
class CacheExpiryTest {

    @Test
    @DisplayName("應可正確建立快取過期設定")
    void shouldCreateCacheExpiry() {
        LocalDateTime now = LocalDateTime.now();
        CacheExpiry expiry = new CacheExpiry(3600, now);

        assertThat(expiry.getTtlSeconds()).isEqualTo(3600);
        assertThat(expiry.getCreatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("TTL 不可為零或負數")
    void shouldRejectNonPositiveTtl() {
        assertThatThrownBy(() -> new CacheExpiry(0, LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TTL seconds must be positive");

        assertThatThrownBy(() -> new CacheExpiry(-1, LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("TTL seconds must be positive");
    }

    @Test
    @DisplayName("未過期的快取應回傳 false")
    void shouldNotBeExpired() {
        CacheExpiry expiry = new CacheExpiry(3600, LocalDateTime.now());

        assertThat(expiry.isExpired()).isFalse();
    }

    @Test
    @DisplayName("已過期的快取應回傳 true")
    void shouldBeExpired() {
        // 建立時間為 10 秒前，TTL 為 5 秒 -> 已過期
        CacheExpiry expiry = new CacheExpiry(5, LocalDateTime.now().minusSeconds(10));

        assertThat(expiry.isExpired()).isTrue();
    }

    @Test
    @DisplayName("邊界情況：剛好在 TTL 範圍內應未過期")
    void shouldNotBeExpiredAtBoundary() {
        // 建立時間為 2 秒前，TTL 為 3600 秒 -> 未過期
        CacheExpiry expiry = new CacheExpiry(3600, LocalDateTime.now().minusSeconds(2));

        assertThat(expiry.isExpired()).isFalse();
    }
}
