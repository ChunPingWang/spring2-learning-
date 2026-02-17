package com.mes.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * [Spring Cloud: Gateway 入口點]
 * [SOLID: SRP - 只負責啟動 API 閘道應用程式]
 *
 * MES API 閘道模組的啟動類別。
 * Module 6 展示 Spring Cloud Gateway 路由、斷路器、WebClient 服務聚合。
 *
 * <p>本模組基於 Spring WebFlux（Reactor Netty），不使用 WebMVC。
 * 所有端點回傳 {@code Mono} 或 {@code Flux} 反應式型別。</p>
 *
 * <h3>教學重點</h3>
 * <ul>
 *     <li>Spring Cloud Gateway 路由配置（Java DSL）</li>
 *     <li>全域過濾器（GlobalFilter）與自訂過濾器工廠</li>
 *     <li>Resilience4j 斷路器整合</li>
 *     <li>WebClient 反應式服務聚合</li>
 *     <li>OpenFeign 參考範例（僅供教學，不在本模組啟用）</li>
 * </ul>
 */
@SpringBootApplication
public class MesGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(MesGatewayApplication.class, args);
    }
}
