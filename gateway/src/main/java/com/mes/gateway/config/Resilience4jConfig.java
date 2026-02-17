package com.mes.gateway.config;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * [Spring Cloud: Resilience4j 斷路器配置]
 * [DDD Pattern: Anti-Corruption Layer - 保護上游服務不受下游故障影響]
 * [SOLID: OCP - 可透過新增 Customizer 擴展不同服務的斷路器策略]
 *
 * 配置反應式斷路器工廠的預設行為。
 *
 * <h3>斷路器參數說明</h3>
 * <ul>
 *     <li>failureRateThreshold=50：失敗率達 50% 時斷路器開啟</li>
 *     <li>waitDurationInOpenState=10s：斷路器開啟後等待 10 秒再嘗試半開</li>
 *     <li>slidingWindowSize=10：滑動視窗大小為 10 次呼叫</li>
 *     <li>minimumNumberOfCalls=5：至少 5 次呼叫後才計算失敗率</li>
 * </ul>
 *
 * <h3>教學重點</h3>
 * <ul>
 *     <li>斷路器三態轉換：CLOSED → OPEN → HALF_OPEN</li>
 *     <li>ReactiveResilience4JCircuitBreakerFactory 與 Gateway 整合</li>
 *     <li>TimeLimiter 設定呼叫逾時</li>
 * </ul>
 */
@Configuration
public class Resilience4jConfig {

    /**
     * [Spring Cloud: 反應式斷路器工廠自訂器]
     *
     * 為所有斷路器設定預設配置。
     * Gateway 的 CircuitBreaker 過濾器會使用此工廠建立斷路器實例。
     *
     * @return 斷路器工廠自訂器
     */
    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom()
                        .failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .slidingWindowSize(10)
                        .minimumNumberOfCalls(5)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom()
                        .timeoutDuration(Duration.ofSeconds(3))
                        .build())
                .build());
    }
}
