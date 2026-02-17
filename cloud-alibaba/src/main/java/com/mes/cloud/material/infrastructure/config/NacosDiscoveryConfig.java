package com.mes.cloud.material.infrastructure.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * [Spring Cloud Alibaba: Nacos 服務發現 - 配置]
 * [SOLID: SRP - 只負責服務發現相關的配置與元資料管理]
 *
 * <h2>教學重點：Nacos 服務發現</h2>
 *
 * <h3>@EnableDiscoveryClient 說明</h3>
 * <p>
 * 在 Spring Cloud 2020+ 版本中，不需要顯式添加 @EnableDiscoveryClient。
 * 只要在 classpath 中有服務發現的 starter（如 spring-cloud-starter-alibaba-nacos-discovery），
 * Spring Boot 就會自動啟用服務發現。這是 Spring Boot 自動配置的特性。
 * </p>
 *
 * <h3>服務註冊概念</h3>
 * <ol>
 *   <li><b>服務註冊</b>: 應用啟動時自動向 Nacos Server 註冊自己的 IP、Port、服務名稱</li>
 *   <li><b>健康檢查</b>: Nacos 定期對已註冊的服務發送心跳檢測，不健康的實例會被移除</li>
 *   <li><b>服務發現</b>: 消費者從 Nacos 查詢服務提供者的可用實例列表</li>
 *   <li><b>元資料 (Metadata)</b>: 可附加額外資訊（如版本、環境等），用於精細化的服務路由</li>
 * </ol>
 *
 * <h3>Nacos 與 Eureka 的差異</h3>
 * <ul>
 *   <li>Nacos 同時支援 CP 和 AP 模式，Eureka 只支援 AP</li>
 *   <li>Nacos 同時提供服務發現和配置中心功能</li>
 *   <li>Nacos 支援命名空間 (Namespace) 和分組 (Group) 進行多環境隔離</li>
 * </ul>
 */
@Configuration
public class NacosDiscoveryConfig {

    private static final Logger log = LoggerFactory.getLogger(NacosDiscoveryConfig.class);

    @Value("${spring.application.name:mes-cloud-alibaba}")
    private String applicationName;

    @Value("${server.port:8086}")
    private int serverPort;

    /**
     * 服務元資料。
     * 可在 Nacos Console 中查看，用於精細化的服務路由。
     *
     * @return 元資料 Map
     */
    public Map<String, String> getServiceMetadata() {
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put("module", "material-management");
        metadata.put("version", "1.0.0");
        metadata.put("domain", "material");
        return metadata;
    }

    @PostConstruct
    public void init() {
        log.info("=== Nacos 服務發現配置 ===");
        log.info("服務名稱: {}", applicationName);
        log.info("服務埠: {}", serverPort);
        log.info("服務元資料: {}", getServiceMetadata());
    }
}
