package com.mes.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * [Spring Cloud: GatewayFilterFactory - 自訂路由過濾器工廠]
 * [SOLID: OCP - 可配置化的過濾器，透過 Config 類別擴展行為]
 *
 * 為每個請求新增唯一的 {@code X-Request-Id} 標頭（UUID）。
 * 繼承 {@link AbstractGatewayFilterFactory}，展示如何建立可在路由配置中使用的自訂過濾器。
 *
 * <h3>使用方式（在 RouteLocator 或 YAML 中）</h3>
 * <pre>
 * .filters(f -> f.filter(addRequestIdFilter.apply(new Config())))
 *
 * # 或在 YAML 中：
 * filters:
 *   - name: AddRequestId
 *     args:
 *       headerName: X-Request-Id
 * </pre>
 *
 * <h3>教學重點</h3>
 * <ul>
 *     <li>AbstractGatewayFilterFactory 的擴展方式</li>
 *     <li>靜態內部 Config 類別定義過濾器參數</li>
 *     <li>GatewayFilter 與 GlobalFilter 的差異（路由級 vs 全域級）</li>
 *     <li>分散式追蹤的基礎：Request ID 傳播</li>
 * </ul>
 */
@Component
public class AddRequestIdFilter extends AbstractGatewayFilterFactory<AddRequestIdFilter.Config> {

    private static final Logger log = LoggerFactory.getLogger(AddRequestIdFilter.class);

    /** 預設的請求 ID 標頭名稱 */
    static final String DEFAULT_HEADER_NAME = "X-Request-Id";

    public AddRequestIdFilter() {
        super(Config.class);
    }

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            String requestId = UUID.randomUUID().toString();
            String headerName = config.getHeaderName() != null
                    ? config.getHeaderName()
                    : DEFAULT_HEADER_NAME;

            log.debug("Adding {} = {} to request", headerName, requestId);

            ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                    .header(headerName, requestId)
                    .build();

            return chain.filter(exchange.mutate().request(mutatedRequest).build());
        };
    }

    /**
     * [Spring Cloud: GatewayFilterFactory 配置類別]
     *
     * 定義過濾器的可配置參數。
     * 在 YAML 或 Java DSL 中可設定 headerName 自訂標頭名稱。
     */
    public static class Config {

        /** 請求 ID 的標頭名稱，預設為 X-Request-Id */
        private String headerName = DEFAULT_HEADER_NAME;

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }
    }
}
