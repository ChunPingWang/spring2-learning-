package com.mes.gateway.feign.reference;

import java.util.List;
import java.util.Map;

/**
 * [Spring Cloud: OpenFeign 參考範例 - 設備服務客戶端]
 * [SOLID: ISP - 介面只定義客戶端實際需要的方法]
 * [DDD Pattern: Anti-Corruption Layer - Feign 客戶端作為外部服務的防腐層]
 *
 * <strong>注意：這是一個僅供教學的參考範例！</strong>
 *
 * <p>在 WebFlux Gateway 中，<strong>不能</strong>使用 {@code @FeignClient}。
 * 請參考 {@link ProductionFeignClient} 的說明了解原因。</p>
 *
 * <h3>在 WebMVC 模組中的使用方式</h3>
 * <pre>
 * &#64;FeignClient(
 *     name = "equipment-service",
 *     url = "http://localhost:8082",
 *     fallbackFactory = FeignClientFallbackExample.EquipmentFallbackFactory.class
 * )
 * public interface EquipmentFeignClient {
 *     ...
 * }
 * </pre>
 *
 * <h3>教學重點</h3>
 * <ul>
 *     <li>@FeignClient 的 name 屬性對應服務註冊名稱</li>
 *     <li>@FeignClient 的 url 屬性直接指定服務位址（無服務發現時）</li>
 *     <li>多個 @FeignClient 介面可對應不同的微服務</li>
 * </ul>
 */
// @FeignClient(name = "equipment-service", url = "http://localhost:8082",
//              fallbackFactory = FeignClientFallbackExample.EquipmentFallbackFactory.class)
public interface EquipmentFeignClient {

    /**
     * 根據 ID 取得設備資訊。
     *
     * <p>對應的 WebClient 寫法：</p>
     * <pre>
     * webClient.get()
     *     .uri("http://localhost:8082/api/v1/equipment/{id}", id)
     *     .retrieve()
     *     .bodyToMono(Map.class);
     * </pre>
     *
     * @param id 設備 ID
     * @return 設備資料
     */
    // @GetMapping("/api/v1/equipment/{id}")
    Map<String, Object> getEquipmentById(/* @PathVariable("id") */ String id);

    /**
     * 取得所有設備列表。
     *
     * @return 設備列表
     */
    // @GetMapping("/api/v1/equipment")
    List<Map<String, Object>> listEquipment();

    /**
     * 依狀態查詢設備列表。
     *
     * @param status 設備狀態
     * @return 符合條件的設備列表
     */
    // @GetMapping("/api/v1/equipment?status={status}")
    List<Map<String, Object>> listEquipmentByStatus(/* @RequestParam("status") */ String status);
}
