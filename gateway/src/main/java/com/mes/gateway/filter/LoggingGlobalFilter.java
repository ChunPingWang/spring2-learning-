package com.mes.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * [Spring Cloud: GlobalFilter - 全域日誌過濾器]
 * [SOLID: SRP - 只負責記錄請求與回應的日誌資訊]
 *
 * 記錄每個通過閘道的請求資訊與回應狀態。
 * 使用 {@code chain.filter(exchange).then(Mono.fromRunnable(...))} 模式，
 * 在回應完成後記錄回應狀態碼與處理時間。
 *
 * <h3>記錄內容</h3>
 * <ul>
 *     <li>請求方法（GET/POST/PUT/DELETE）</li>
 *     <li>請求路徑</li>
 *     <li>請求標頭</li>
 *     <li>回應狀態碼</li>
 *     <li>處理時間（毫秒）</li>
 * </ul>
 *
 * <h3>教學重點</h3>
 * <ul>
 *     <li>GlobalFilter 對所有路由生效</li>
 *     <li>Ordered 介面控制過濾器執行順序（數字越小越先執行）</li>
 *     <li>反應式過濾器鏈的 pre/post 處理模式</li>
 * </ul>
 */
@Component
public class LoggingGlobalFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(LoggingGlobalFilter.class);

    /** 交換屬性鍵：請求開始時間戳 */
    private static final String START_TIME_ATTR = "loggingFilterStartTime";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        long startTime = System.currentTimeMillis();
        exchange.getAttributes().put(START_TIME_ATTR, startTime);

        // Pre-filter: 記錄請求資訊
        log.debug("Gateway Request: {} {} | Headers: {}",
                request.getMethod(),
                request.getPath(),
                formatHeaders(request.getHeaders()));

        // Post-filter: 記錄回應資訊與處理時間
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            ServerHttpResponse response = exchange.getResponse();
            Long start = exchange.getAttribute(START_TIME_ATTR);
            long duration = start != null ? System.currentTimeMillis() - start : -1;

            log.debug("Gateway Response: {} {} | Status: {} | Duration: {}ms",
                    request.getMethod(),
                    request.getPath(),
                    response.getStatusCode(),
                    duration);
        }));
    }

    /**
     * 過濾器順序：-1（最高優先度）。
     * 確保日誌記錄在其他過濾器之前開始，之後結束。
     *
     * @return 過濾器順序值
     */
    @Override
    public int getOrder() {
        return -1;
    }

    /**
     * 格式化 HTTP 標頭為可讀字串，隱藏敏感標頭值。
     *
     * @param headers HTTP 標頭
     * @return 格式化後的標頭字串
     */
    private String formatHeaders(HttpHeaders headers) {
        StringBuilder sb = new StringBuilder("{");
        headers.forEach((name, values) -> {
            if ("Authorization".equalsIgnoreCase(name)) {
                sb.append(name).append("=[REDACTED], ");
            } else {
                sb.append(name).append("=").append(values).append(", ");
            }
        });
        if (sb.length() > 1) {
            sb.setLength(sb.length() - 2);
        }
        sb.append("}");
        return sb.toString();
    }
}
