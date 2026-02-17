package com.mes.gateway.feign.reference;

import java.util.List;
import java.util.Map;

/**
 * [Spring Cloud: OpenFeign 參考範例 - 生產服務客戶端]
 * [SOLID: ISP - 介面只定義客戶端實際需要的方法]
 * [DDD Pattern: Anti-Corruption Layer - Feign 客戶端作為外部服務的防腐層]
 *
 * <strong>注意：這是一個僅供教學的參考範例！</strong>
 *
 * <p>在 WebFlux Gateway 中，<strong>不能</strong>使用 {@code @FeignClient}。
 * OpenFeign 依賴 WebMVC（Servlet），與 WebFlux 不相容。
 * 在本模組中，服務呼叫應使用 {@code WebClient}。</p>
 *
 * <p>此介面展示在 WebMVC 微服務中如何使用 {@code @FeignClient}：</p>
 *
 * <h3>在 WebMVC 模組中的使用方式</h3>
 * <pre>
 * // 1. 在 pom.xml 加入 spring-cloud-starter-openfeign
 * // 2. 在啟動類別加上 @EnableFeignClients
 * // 3. 取消下方 @FeignClient 註解的註解
 *
 * &#64;FeignClient(
 *     name = "production-service",
 *     url = "http://localhost:8081",
 *     fallbackFactory = FeignClientFallbackExample.ProductionFallbackFactory.class
 * )
 * public interface ProductionFeignClient {
 *     ...
 * }
 * </pre>
 *
 * <h3>教學重點</h3>
 * <ul>
 *     <li>@FeignClient 宣告式 HTTP 客戶端</li>
 *     <li>Spring MVC 注解（@GetMapping, @PostMapping）定義端點</li>
 *     <li>fallbackFactory 整合 Resilience4j 降級</li>
 *     <li>@PathVariable 路徑參數對應</li>
 * </ul>
 */
// @FeignClient(name = "production-service", url = "http://localhost:8081",
//              fallbackFactory = FeignClientFallbackExample.ProductionFallbackFactory.class)
public interface ProductionFeignClient {

    /**
     * 根據 ID 取得生產紀錄。
     *
     * <p>對應的 WebClient 寫法：</p>
     * <pre>
     * webClient.get()
     *     .uri("http://localhost:8081/api/v1/productions/{id}", id)
     *     .retrieve()
     *     .bodyToMono(Map.class);
     * </pre>
     *
     * @param id 生產紀錄 ID
     * @return 生產紀錄資料
     */
    // @GetMapping("/api/v1/productions/{id}")
    Map<String, Object> getProductionById(/* @PathVariable("id") */ String id);

    /**
     * 取得所有生產紀錄列表。
     *
     * @return 生產紀錄列表
     */
    // @GetMapping("/api/v1/productions")
    List<Map<String, Object>> listProductions();

    /**
     * 建立新的生產紀錄。
     *
     * @param request 建立請求
     * @return 建立結果
     */
    // @PostMapping("/api/v1/productions")
    Map<String, Object> createProduction(/* @RequestBody */ Map<String, Object> request);
}
