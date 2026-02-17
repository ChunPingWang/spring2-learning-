package com.mes.redis.dashboard.infrastructure.config;

import com.mes.redis.dashboard.domain.service.DashboardDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * [Infrastructure: 看板模組設定]
 * [SOLID: SRP - 只負責看板模組的基礎設施配置]
 *
 * 看板模組的 Spring 設定類別。
 * 將領域層的 Domain Service 註冊為 Spring Bean。
 *
 * 教學重點：
 * 領域層的 DashboardDomainService 不使用任何 Spring 註解，
 * 保持領域層的純粹性。透過此配置類別，由基礎設施層負責將其註冊到 Spring 容器。
 */
@Configuration
public class DashboardModuleConfig {

    private static final Logger log = LoggerFactory.getLogger(DashboardModuleConfig.class);

    /**
     * 註冊 DashboardDomainService 為 Spring Bean。
     * 領域層的服務不依賴 Spring，但需要透過基礎設施層配置來註冊。
     *
     * @return DashboardDomainService 實例
     */
    @Bean
    public DashboardDomainService dashboardDomainService() {
        log.info("Registering DashboardDomainService bean");
        return new DashboardDomainService();
    }
}
