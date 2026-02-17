package com.mes.gateway.aggregation;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

/**
 * [Spring Cloud: Gateway Aggregation Route 配置]
 * [SOLID: SRP - 只負責註冊聚合端點的路由函式]
 *
 * 使用 WebFlux 的 {@link RouterFunction} 註冊閘道聚合端點。
 * 此端點不走 Gateway 路由，而是由閘道自身處理，直接聚合多個下游服務的資料。
 *
 * <h3>教學重點</h3>
 * <ul>
 *     <li>RouterFunction 函式式路由 vs @Controller 注解式路由</li>
 *     <li>閘道聚合端點：減少客戶端多次呼叫</li>
 *     <li>ServerResponse 建構回應</li>
 * </ul>
 */
@Configuration
public class AggregationRouteConfig {

    /**
     * [Spring WebFlux: RouterFunction Bean]
     *
     * 註冊 {@code /api/v1/gateway/dashboard} 端點，
     * 呼叫 {@link DashboardAggregationHandler} 聚合多個服務的資料。
     *
     * @param handler 儀表板聚合處理器
     * @return 路由函式
     */
    @Bean
    public RouterFunction<ServerResponse> aggregationRoutes(DashboardAggregationHandler handler) {
        return RouterFunctions.route()
                .GET("/api/v1/gateway/dashboard", request ->
                        handler.aggregateDashboard()
                                .flatMap(view -> ServerResponse.ok()
                                        .contentType(MediaType.APPLICATION_JSON)
                                        .bodyValue(view)))
                .build();
    }
}
