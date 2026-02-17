package com.mes.gateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import reactor.core.publisher.Mono;

/**
 * [Spring Cloud: Gateway 限流配置]
 * [SOLID: SRP - 只負責定義限流的 Key 解析策略]
 *
 * 定義限流的 Key 解析器（{@link KeyResolver}）。
 * Gateway 的 RequestRateLimiter 過濾器使用 KeyResolver 確定限流的維度。
 *
 * <h3>教學重點</h3>
 * <ul>
 *     <li>KeyResolver 決定「按什麼維度限流」（IP、路徑、使用者等）</li>
 *     <li>實際限流需搭配 Redis（spring-boot-starter-data-redis-reactive）</li>
 *     <li>本模組僅展示 KeyResolver 定義方式，未啟用完整限流鏈路</li>
 * </ul>
 *
 * <h3>常見限流策略</h3>
 * <ul>
 *     <li>IP 限流：防止單一來源過多請求</li>
 *     <li>路徑限流：保護特定 API 端點</li>
 *     <li>使用者限流：依據認證身分限制呼叫頻率</li>
 * </ul>
 */
@Configuration
public class RateLimiterConfig {

    /**
     * [Spring Cloud: IP 維度的 Key 解析器]
     *
     * 從請求中提取客戶端 IP 位址作為限流鍵。
     * 優先取 X-Forwarded-For 標頭（反向代理場景），
     * 否則取遠端位址。
     *
     * @return 基於 IP 的 KeyResolver
     */
    @Bean
    @Primary
    public KeyResolver ipKeyResolver() {
        return exchange -> {
            String ip = exchange.getRequest().getHeaders().getFirst("X-Forwarded-For");
            if (ip == null || ip.isEmpty()) {
                ip = exchange.getRequest().getRemoteAddress() != null
                        ? exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
                        : "unknown";
            }
            return Mono.just(ip);
        };
    }

    /**
     * [Spring Cloud: 路徑維度的 Key 解析器]
     *
     * 從請求中提取路徑作為限流鍵。
     * 適用於保護特定 API 端點不被過度呼叫。
     *
     * @return 基於路徑的 KeyResolver
     */
    @Bean
    public KeyResolver pathKeyResolver() {
        return exchange -> Mono.just(exchange.getRequest().getPath().value());
    }
}
