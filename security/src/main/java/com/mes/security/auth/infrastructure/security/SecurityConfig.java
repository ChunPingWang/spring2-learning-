package com.mes.security.auth.infrastructure.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

/**
 * [Hexagonal Architecture: Infrastructure Config - Spring Security 設定]
 * [SOLID: SRP - 只負責 Web 安全過濾鏈的設定]
 * [SOLID: OCP - 可透過新增設定擴展安全規則]
 *
 * 設定 Spring Security 的安全過濾鏈：
 * 1. 停用 CSRF（因為使用 JWT 無狀態認證）
 * 2. 設定 Session 為無狀態 (STATELESS)
 * 3. 公開端點：/api/v1/auth/** 不需認證
 * 4. 其他端點需要認證
 * 5. 使用 JWT Resource Server 進行 Token 驗證
 * 6. 設定 JwtAuthenticationConverter 將 roles claim 轉為 ROLE_ 前綴的 authorities
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    /**
     * [SOLID: SRP - SecurityFilterChain Bean 只負責 HTTP 安全設定]
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                    .antMatchers("/api/v1/auth/**").permitAll()
                    .anyRequest().authenticated()
                .and()
                .oauth2ResourceServer()
                    .jwt()
                    .jwtAuthenticationConverter(jwtAuthenticationConverter());

        return http.build();
    }

    /**
     * [SOLID: SRP - 只負責 JWT Token 到 Spring Security Authentication 的轉換]
     *
     * 將 JWT 中的 "roles" claim 提取並映射為 ROLE_ 前綴的 GrantedAuthority。
     */
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles");
        grantedAuthoritiesConverter.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
