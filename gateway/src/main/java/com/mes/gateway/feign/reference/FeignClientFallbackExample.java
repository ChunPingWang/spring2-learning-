package com.mes.gateway.feign.reference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * [Spring Cloud: OpenFeign FallbackFactory 參考範例]
 * [SOLID: OCP - 透過 FallbackFactory 擴展降級行為，不修改原始介面]
 * [DDD Pattern: Anti-Corruption Layer - 降級回應防止故障擴散]
 *
 * <strong>注意：這是一個僅供教學的參考範例！</strong>
 *
 * <p>展示 OpenFeign 搭配 Resilience4j 的 FallbackFactory 降級模式。
 * FallbackFactory 比 Fallback 類別更強大，因為它可以取得導致降級的例外資訊。</p>
 *
 * <h3>在 WebMVC 模組中的使用方式</h3>
 * <pre>
 * // 1. 實作 FallbackFactory&lt;YourFeignClient&gt;
 * // 2. 加上 @Component 讓 Spring 管理
 * // 3. 在 @FeignClient 中指定 fallbackFactory
 *
 * &#64;Component
 * public class ProductionFallbackFactory
 *         implements FallbackFactory&lt;ProductionFeignClient&gt; {
 *     &#64;Override
 *     public ProductionFeignClient create(Throwable cause) {
 *         return new ProductionFeignClient() { ... };
 *     }
 * }
 * </pre>
 *
 * <h3>FallbackFactory vs Fallback</h3>
 * <ul>
 *     <li>Fallback：只提供降級回應，無法得知失敗原因</li>
 *     <li>FallbackFactory：可取得 Throwable，根據不同例外提供不同降級策略</li>
 * </ul>
 *
 * <h3>教學重點</h3>
 * <ul>
 *     <li>FallbackFactory 的 create(Throwable) 方法</li>
 *     <li>匿名內部類別實作降級邏輯</li>
 *     <li>降級回應中記錄失敗原因（用於診斷）</li>
 * </ul>
 */
public class FeignClientFallbackExample {

    /**
     * 生產服務的 FallbackFactory 範例。
     *
     * <p>在 WebMVC 模組中使用時，需加上 @Component 注解。</p>
     *
     * <pre>
     * // @Component
     * // public class ProductionFallbackFactory
     * //         implements FallbackFactory&lt;ProductionFeignClient&gt; {
     * //
     * //     private static final Logger log = LoggerFactory.getLogger(...);
     * //
     * //     @Override
     * //     public ProductionFeignClient create(Throwable cause) {
     * //         log.error("Production service fallback triggered", cause);
     * //         return new ProductionFeignClient() {
     * //             @Override
     * //             public Map&lt;String, Object&gt; getProductionById(String id) {
     * //                 return buildFallback("getProductionById", cause);
     * //             }
     * //             // ... 其他方法
     * //         };
     * //     }
     * // }
     * </pre>
     */
    // 以下為靜態工具方法，展示降級回應的建構方式

    /**
     * 建構降級回應（範例）。
     *
     * @param method 被降級的方法名稱
     * @param cause  導致降級的例外
     * @return 降級回應 Map
     */
    public static Map<String, Object> buildFallbackResponse(String method, Throwable cause) {
        Map<String, Object> fallback = new HashMap<>();
        fallback.put("fallback", true);
        fallback.put("method", method);
        fallback.put("error", cause != null ? cause.getMessage() : "unknown");
        fallback.put("message", "服務暫時不可用，已啟用降級回應");
        return fallback;
    }

    /**
     * 建構空列表的降級回應（範例）。
     *
     * @return 空列表
     */
    public static List<Map<String, Object>> buildEmptyListFallback() {
        return new ArrayList<>();
    }

    // 此類別不需要被實例化
    private FeignClientFallbackExample() {
    }
}
