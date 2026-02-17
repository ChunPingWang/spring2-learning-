package com.mes.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * [Spring Cloud: GlobalFilter - 認證標頭轉發過濾器]
 * [SOLID: SRP - 只負責將 Authorization 標頭傳遞至下游服務]
 *
 * 提取客戶端請求的 {@code Authorization} 標頭，並轉發至下游微服務。
 * 若請求不包含 Authorization 標頭，則直接放行（不報錯）。
 *
 * <h3>教學重點</h3>
 * <ul>
 *     <li>閘道層級的認證標頭傳播</li>
 *     <li>GlobalFilter 在所有路由上自動生效</li>
 *     <li>使用 mutate() 修改請求（不可變設計）</li>
 * </ul>
 *
 * <h3>設計考量</h3>
 * <p>Gateway 不驗證 Token 的有效性，驗證由下游的 mes-security 模組負責。
 * Gateway 只負責「傳遞」，體現了閘道作為代理的職責邊界。</p>
 */
@Component
public class AuthHeaderRelayFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(AuthHeaderRelayFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String authHeader = exchange.getRequest().getHeaders()
                .getFirst(HttpHeaders.AUTHORIZATION);

        if (authHeader != null && !authHeader.isEmpty()) {
            log.debug("Relaying Authorization header to downstream service");
            // Authorization 標頭已存在於原始請求中，Gateway 會自動轉發
            // 此處示範如何在需要時修改或新增標頭
            ServerWebExchange mutatedExchange = exchange.mutate()
                    .request(exchange.getRequest().mutate()
                            .header(HttpHeaders.AUTHORIZATION, authHeader)
                            .build())
                    .build();
            return chain.filter(mutatedExchange);
        }

        log.debug("No Authorization header found, passing through");
        return chain.filter(exchange);
    }

    /**
     * 過濾器順序：0。
     * 在日誌過濾器之後、計時過濾器之前執行。
     *
     * @return 過濾器順序值
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
