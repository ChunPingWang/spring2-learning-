package com.mes.gateway.fallback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * [Spring Cloud: Circuit Breaker Fallback Controller]
 * [DDD Pattern: Anti-Corruption Layer - 降級回應保護客戶端不受下游故障影響]
 * [SOLID: SRP - 只負責提供斷路器降級回應]
 *
 * 當下游服務不可用且斷路器開啟時，Gateway 將請求轉發至此控制器。
 * 回傳友善的中文錯誤訊息，避免客戶端收到原始的 5xx 錯誤。
 *
 * <h3>教學重點</h3>
 * <ul>
 *     <li>CircuitBreaker fallbackUri 的目標端點</li>
 *     <li>WebFlux 環境中使用 {@code Mono<Map>} 作為回傳型別</li>
 *     <li>依服務類型提供不同的降級訊息</li>
 *     <li>降級回應中包含時間戳，方便排查問題</li>
 * </ul>
 */
@RestController
@RequestMapping("/fallback")
public class FallbackController {

    private static final Logger log = LoggerFactory.getLogger(FallbackController.class);

    /**
     * 生產服務降級端點。
     * 當 mes-web-api（port 8081）不可用時回傳此回應。
     *
     * @return 包含降級訊息的 Mono
     */
    @GetMapping("/production")
    public Mono<Map<String, Object>> productionFallbackGet() {
        return buildFallbackResponse("生產服務暫時不可用", "production-service");
    }

    /**
     * 生產服務降級端點（POST）。
     *
     * @return 包含降級訊息的 Mono
     */
    @PostMapping("/production")
    public Mono<Map<String, Object>> productionFallbackPost() {
        return buildFallbackResponse("生產服務暫時不可用", "production-service");
    }

    /**
     * 設備服務降級端點。
     * 當 mes-mybatis（port 8082）不可用時回傳此回應。
     *
     * @return 包含降級訊息的 Mono
     */
    @GetMapping("/equipment")
    public Mono<Map<String, Object>> equipmentFallbackGet() {
        return buildFallbackResponse("設備服務暫時不可用", "equipment-service");
    }

    /**
     * 設備服務降級端點（POST）。
     *
     * @return 包含降級訊息的 Mono
     */
    @PostMapping("/equipment")
    public Mono<Map<String, Object>> equipmentFallbackPost() {
        return buildFallbackResponse("設備服務暫時不可用", "equipment-service");
    }

    /**
     * 通用降級端點。
     * 當任何未指定專屬降級的服務不可用時回傳此回應。
     *
     * @return 包含降級訊息的 Mono
     */
    @GetMapping("/general")
    public Mono<Map<String, Object>> generalFallbackGet() {
        return buildFallbackResponse("服務暫時不可用，請稍後重試", "general");
    }

    /**
     * 通用降級端點（POST）。
     *
     * @return 包含降級訊息的 Mono
     */
    @PostMapping("/general")
    public Mono<Map<String, Object>> generalFallbackPost() {
        return buildFallbackResponse("服務暫時不可用，請稍後重試", "general");
    }

    /**
     * 建構統一的降級回應。
     *
     * @param message 降級訊息
     * @param service 服務名稱
     * @return 降級回應的 Mono
     */
    private Mono<Map<String, Object>> buildFallbackResponse(String message, String service) {
        log.warn("Fallback triggered for service: {}", service);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 503);
        response.put("message", message);
        response.put("service", service);
        response.put("timestamp", LocalDateTime.now().toString());

        return Mono.just(response);
    }
}
