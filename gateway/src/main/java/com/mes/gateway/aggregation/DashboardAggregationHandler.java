package com.mes.gateway.aggregation;

import com.mes.gateway.aggregation.dto.AggregatedDashboardView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * [Spring Cloud: Gateway Aggregation Pattern - 服務聚合處理器]
 * [DDD Pattern: Application Service - 協調多個下游服務的呼叫]
 * [SOLID: SRP - 只負責聚合儀表板所需的多來源資料]
 *
 * 使用 {@link WebClient} 以反應式方式並行呼叫多個下游微服務，
 * 並透過 {@link Mono#zip} 將結果合併為統一的儀表板檢視。
 *
 * <h3>教學重點</h3>
 * <ul>
 *     <li>WebClient 在 WebFlux 環境中的使用</li>
 *     <li>Mono.zip() 實現並行呼叫與結果合併</li>
 *     <li>onErrorResume() 處理部分服務失敗的降級策略</li>
 *     <li>Gateway Aggregation Pattern 減少客戶端往返次數</li>
 * </ul>
 *
 * <h3>設計考量</h3>
 * <p>當某個下游服務不可用時，使用 onErrorResume 回傳空的預設值，
 * 確保其他服務的資料仍然可以正常回傳（部分降級）。</p>
 */
@Component
public class DashboardAggregationHandler {

    private static final Logger log = LoggerFactory.getLogger(DashboardAggregationHandler.class);

    private final WebClient webClient;

    /** mes-web-api 生產服務基礎 URL */
    private static final String PRODUCTION_SERVICE_URL = "http://localhost:8081";

    /** mes-mybatis 設備服務基礎 URL */
    private static final String EQUIPMENT_SERVICE_URL = "http://localhost:8082";

    public DashboardAggregationHandler(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * [Spring Cloud: 反應式服務聚合]
     *
     * 並行呼叫生產服務與設備服務，合併為統一的儀表板檢視。
     * 使用 {@link Mono#zip} 實現非阻塞的並行呼叫。
     *
     * @return 聚合後的儀表板檢視
     */
    public Mono<AggregatedDashboardView> aggregateDashboard() {
        log.debug("Starting dashboard aggregation");

        Mono<Map<String, Object>> productionMono = fetchProductionSummary();
        Mono<Map<String, Object>> equipmentMono = fetchEquipmentStatus();

        return Mono.zip(productionMono, equipmentMono)
                .map(tuple -> new AggregatedDashboardView(
                        tuple.getT1(),
                        tuple.getT2(),
                        LocalDateTime.now().toString()));
    }

    /**
     * 呼叫生產服務取得生產摘要。
     * 若服務不可用則回傳預設的空資料（部分降級）。
     *
     * @return 生產摘要資料
     */
    private Mono<Map<String, Object>> fetchProductionSummary() {
        ParameterizedTypeReference<Map<String, Object>> typeRef =
                new ParameterizedTypeReference<Map<String, Object>>() {};

        return webClient.get()
                .uri(PRODUCTION_SERVICE_URL + "/api/v1/productions")
                .retrieve()
                .bodyToMono(typeRef)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.warn("Failed to fetch production summary: {}", ex.getMessage());
                    return Mono.just(buildFallbackMap("production", "服務暫時不可用"));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.warn("Failed to connect to production service: {}", ex.getMessage());
                    return Mono.just(buildFallbackMap("production", "無法連線至服務"));
                });
    }

    /**
     * 呼叫設備服務取得設備狀態。
     * 若服務不可用則回傳預設的空資料（部分降級）。
     *
     * @return 設備狀態資料
     */
    private Mono<Map<String, Object>> fetchEquipmentStatus() {
        ParameterizedTypeReference<Map<String, Object>> typeRef =
                new ParameterizedTypeReference<Map<String, Object>>() {};

        return webClient.get()
                .uri(EQUIPMENT_SERVICE_URL + "/api/v1/equipment")
                .retrieve()
                .bodyToMono(typeRef)
                .onErrorResume(WebClientResponseException.class, ex -> {
                    log.warn("Failed to fetch equipment status: {}", ex.getMessage());
                    return Mono.just(buildFallbackMap("equipment", "服務暫時不可用"));
                })
                .onErrorResume(Exception.class, ex -> {
                    log.warn("Failed to connect to equipment service: {}", ex.getMessage());
                    return Mono.just(buildFallbackMap("equipment", "無法連線至服務"));
                });
    }

    /**
     * 建構降級時的預設回應資料。
     *
     * @param service 服務名稱
     * @param message 降級訊息
     * @return 預設的降級資料 Map
     */
    private Map<String, Object> buildFallbackMap(String service, String message) {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("service", service);
        fallback.put("status", "unavailable");
        fallback.put("message", message);
        return fallback;
    }
}
