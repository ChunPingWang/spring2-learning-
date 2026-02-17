package com.mes.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * [Spring Cloud: Gateway Route Configuration]
 * [DDD Pattern: Anti-Corruption Layer - 閘道作為上下文映射的防腐層]
 * [SOLID: OCP - 新增路由不需修改既有路由配置]
 *
 * 使用 Java DSL 定義 API 閘道路由規則。
 * 每個路由包含：Path 斷言、RewritePath 過濾器、CircuitBreaker 斷路器降級。
 *
 * <h3>路由對照表</h3>
 * <table>
 *     <tr><th>路徑前綴</th><th>下游服務</th><th>埠號</th></tr>
 *     <tr><td>/api/v1/productions/**</td><td>mes-web-api</td><td>8081</td></tr>
 *     <tr><td>/api/v1/equipment/**</td><td>mes-mybatis</td><td>8082</td></tr>
 *     <tr><td>/api/v1/inspections/**</td><td>mes-kafka</td><td>8083</td></tr>
 *     <tr><td>/api/v1/users/**, /api/v1/auth/**</td><td>mes-security</td><td>8084</td></tr>
 *     <tr><td>/api/v1/dashboard/**</td><td>mes-redis</td><td>8085</td></tr>
 * </table>
 *
 * <h3>教學重點</h3>
 * <ul>
 *     <li>RouteLocatorBuilder Java DSL 語法</li>
 *     <li>Path 斷言（Predicate）匹配請求路徑</li>
 *     <li>RewritePath 過濾器轉換下游路徑</li>
 *     <li>CircuitBreaker 過濾器實現降級回退</li>
 * </ul>
 */
@Configuration
public class RouteConfig {

    /**
     * [Spring Cloud: RouteLocator Bean]
     *
     * 定義所有 MES 微服務的路由規則。
     * 使用 Java DSL 風格，比 YAML 配置更具型別安全性。
     *
     * @param builder Spring Cloud Gateway 提供的路由建構器
     * @return 配置好的路由定位器
     */
    @Bean
    public RouteLocator mesRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // ---- 生產服務路由 (mes-web-api) ----
                .route("production-service", r -> r
                        .path("/api/v1/productions/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/productions/(?<segment>.*)",
                                        "/api/v1/productions/${segment}")
                                .circuitBreaker(cb -> cb
                                        .setName("productionCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/production")))
                        .uri("http://localhost:8081"))

                // ---- 設備服務路由 (mes-mybatis) ----
                .route("equipment-service", r -> r
                        .path("/api/v1/equipment/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/equipment/(?<segment>.*)",
                                        "/api/v1/equipment/${segment}")
                                .circuitBreaker(cb -> cb
                                        .setName("equipmentCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/equipment")))
                        .uri("http://localhost:8082"))

                // ---- 品質檢驗服務路由 (mes-kafka) ----
                .route("inspection-service", r -> r
                        .path("/api/v1/inspections/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/inspections/(?<segment>.*)",
                                        "/api/v1/inspections/${segment}")
                                .circuitBreaker(cb -> cb
                                        .setName("inspectionCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/general")))
                        .uri("http://localhost:8083"))

                // ---- 使用者服務路由 (mes-security) ----
                .route("user-service", r -> r
                        .path("/api/v1/users/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/users/(?<segment>.*)",
                                        "/api/v1/users/${segment}")
                                .circuitBreaker(cb -> cb
                                        .setName("userCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/general")))
                        .uri("http://localhost:8084"))

                // ---- 認證服務路由 (mes-security) ----
                .route("auth-service", r -> r
                        .path("/api/v1/auth/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/auth/(?<segment>.*)",
                                        "/api/v1/auth/${segment}")
                                .circuitBreaker(cb -> cb
                                        .setName("authCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/general")))
                        .uri("http://localhost:8084"))

                // ---- 儀表板服務路由 (mes-redis) ----
                .route("dashboard-service", r -> r
                        .path("/api/v1/dashboard/**")
                        .filters(f -> f
                                .rewritePath("/api/v1/dashboard/(?<segment>.*)",
                                        "/api/v1/dashboard/${segment}")
                                .circuitBreaker(cb -> cb
                                        .setName("dashboardCircuitBreaker")
                                        .setFallbackUri("forward:/fallback/general")))
                        .uri("http://localhost:8085"))

                .build();
    }
}
