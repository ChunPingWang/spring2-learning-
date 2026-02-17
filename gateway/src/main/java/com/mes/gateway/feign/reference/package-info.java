/**
 * OpenFeign 參考範例 - 僅作為教學展示，不在本模組中啟用。
 *
 * <h3>重要說明</h3>
 * <p>Spring Cloud Gateway 基於 WebFlux（Reactor Netty），
 * 而 OpenFeign 基於 WebMVC（Servlet）。兩者<strong>無法共存</strong>於同一個 Spring Boot 應用程式。</p>
 *
 * <h3>正確的使用方式</h3>
 * <ul>
 *     <li>在 Gateway（WebFlux）模組中：使用 {@code WebClient} 呼叫下游服務</li>
 *     <li>在其他微服務（WebMVC）模組中：可使用 {@code @FeignClient} 呼叫其他服務</li>
 * </ul>
 *
 * <h3>本套件用途</h3>
 * <p>此套件中的介面展示 OpenFeign 的常見使用模式，包含：</p>
 * <ul>
 *     <li>{@code @FeignClient} 宣告式 HTTP 客戶端</li>
 *     <li>FallbackFactory 降級工廠模式</li>
 *     <li>與 Resilience4j 斷路器整合</li>
 * </ul>
 *
 * <p>這些類別的注解已被註解掉（commented out），不會被 Spring 掃描載入。
 * 學習者可將這些模式應用於 WebMVC 模組（如 mes-web-api、mes-mybatis 等）。</p>
 *
 * [Spring Cloud: OpenFeign 教學參考]
 * [SOLID: ISP - 以介面定義服務契約，客戶端只依賴需要的方法]
 */
package com.mes.gateway.feign.reference;
