package com.mes.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * [Spring Cloud: GlobalFilter - 請求計時過濾器]
 * [SOLID: SRP - 只負責計算並附加請求處理時間]
 *
 * 記錄請求開始時間至交換屬性，並在回應完成後計算處理時間，
 * 將結果寫入 {@code X-Response-Time} 回應標頭。
 *
 * <h3>教學重點</h3>
 * <ul>
 *     <li>exchange.getAttributes() 用於在過濾器間共享資料</li>
 *     <li>then(Mono.fromRunnable(...)) 模式實現 post-filter 邏輯</li>
 *     <li>自訂回應標頭（X-Response-Time）</li>
 * </ul>
 */
@Component
public class RequestTimingFilter implements GlobalFilter, Ordered {

    private static final Logger log = LoggerFactory.getLogger(RequestTimingFilter.class);

    /** 交換屬性鍵：請求開始時間戳 */
    static final String START_TIME_ATTR = "requestTimingStartTime";

    /** 回應標頭名稱：處理時間（毫秒） */
    static final String RESPONSE_TIME_HEADER = "X-Response-Time";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Pre-filter: 記錄開始時間
        exchange.getAttributes().put(START_TIME_ATTR, System.currentTimeMillis());

        // Post-filter: 計算處理時間並寫入回應標頭
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            Long startTime = exchange.getAttribute(START_TIME_ATTR);
            if (startTime != null) {
                long duration = System.currentTimeMillis() - startTime;
                exchange.getResponse().getHeaders()
                        .add(RESPONSE_TIME_HEADER, duration + "ms");
                log.debug("Request {} took {}ms",
                        exchange.getRequest().getPath(), duration);
            }
        }));
    }

    /**
     * 過濾器順序：1。
     * 在認證標頭轉發之後執行。
     *
     * @return 過濾器順序值
     */
    @Override
    public int getOrder() {
        return 1;
    }
}
