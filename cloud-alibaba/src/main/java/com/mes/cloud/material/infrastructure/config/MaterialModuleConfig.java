package com.mes.cloud.material.infrastructure.config;

import com.mes.cloud.material.domain.service.StockDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * [Hexagonal Architecture: Configuration - 組裝基礎設施元件]
 * [SOLID: DIP - 透過 Spring Configuration 進行依賴注入的組裝]
 *
 * Spring Cloud Alibaba 物料模組的 Spring 配置類別。
 * 負責註冊非 @Component 標記的 Bean（如 Domain Service）。
 */
@Configuration
public class MaterialModuleConfig {

    /**
     * 註冊 StockDomainService 為 Spring Bean。
     * Domain Service 使用 @DomainService 標記（非 Spring 註解），
     * 因此需要透過 @Bean 方法手動註冊。
     *
     * @return StockDomainService 實例
     */
    @Bean
    public StockDomainService stockDomainService() {
        return new StockDomainService();
    }
}
