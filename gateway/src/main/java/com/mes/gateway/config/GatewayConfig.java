package com.mes.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Arrays;

/**
 * [Spring Cloud: Gateway 全域配置]
 * [SOLID: SRP - 只負責閘道層級的基礎設施配置]
 *
 * 配置 API 閘道的通用元件：
 * <ul>
 *     <li>{@link WebClient} - 反應式 HTTP 客戶端，用於服務聚合</li>
 *     <li>{@link CorsWebFilter} - 跨來源資源共享（CORS）過濾器</li>
 * </ul>
 *
 * <h3>教學重點</h3>
 * <ul>
 *     <li>WebFlux 環境下使用 WebClient 取代 RestTemplate</li>
 *     <li>反應式 CORS 配置與 WebMVC 的差異</li>
 *     <li>閘道層級的全域逾時設定</li>
 * </ul>
 */
@Configuration
public class GatewayConfig {

    /**
     * [Spring WebFlux: WebClient Bean]
     *
     * 建立全域共用的 WebClient 實例。
     * 在 Gateway（WebFlux）環境中，WebClient 是唯一的 HTTP 客戶端選擇。
     *
     * <p>注意：不能使用 RestTemplate 或 OpenFeign，因為它們依賴 WebMVC。</p>
     *
     * @return 配置好的 WebClient.Builder 實例
     */
    @Bean
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    /**
     * [Spring WebFlux: WebClient 預設實例]
     *
     * 提供即用的 WebClient 實例，注入到需要呼叫下游服務的元件中。
     *
     * @param builder WebClient 建構器
     * @return WebClient 實例
     */
    @Bean
    public WebClient webClient(WebClient.Builder builder) {
        return builder.build();
    }

    /**
     * [Spring WebFlux: CORS 過濾器]
     *
     * 配置跨來源資源共享規則。在 WebFlux 環境中，
     * 使用 {@link CorsWebFilter} 而非 WebMVC 的 CorsFilter。
     *
     * <p>教學注意：生產環境應限制 allowed-origins 為特定域名。</p>
     *
     * @return 反應式 CORS 過濾器
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowedOrigins(Arrays.asList("*"));
        corsConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        corsConfig.setAllowedHeaders(Arrays.asList("*"));
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}
