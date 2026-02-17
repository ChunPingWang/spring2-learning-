package com.mes.security.auth.infrastructure.config;

import com.mes.security.auth.domain.service.AuthenticationDomainService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * [Hexagonal Architecture: Infrastructure Config - 模組設定]
 * [SOLID: SRP - 只負責註冊模組所需的共用 Bean]
 *
 * 註冊安全模組所需的基礎 Bean：
 * 1. BCryptPasswordEncoder - 密碼編碼器
 * 2. AuthenticationDomainService - 認證領域服務
 */
@Configuration
public class SecurityModuleConfig {

    /**
     * BCrypt 密碼編碼器。
     * 被 BcryptPasswordEncoderAdapter 包裝為 PasswordEncoderPort。
     */
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 認證領域服務。
     * 純領域邏輯，不依賴 Spring 框架。
     */
    @Bean
    public AuthenticationDomainService authenticationDomainService() {
        return new AuthenticationDomainService();
    }
}
