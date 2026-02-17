package com.mes.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * [DDD Pattern: Bounded Context - 安全模組入口]
 * [SOLID: SRP - 只負責啟動 Spring Boot 應用]
 *
 * MES 安全模組的 Spring Boot 應用程式入口點。
 * 此模組展示：
 * 1. Spring Security + JWT 的整合
 * 2. OAuth2 Resource Server 設定
 * 3. RBAC（角色基礎存取控制）
 * 4. 六角形架構的安全實作
 */
@SpringBootApplication
public class MesSecurityApplication {

    public static void main(String[] args) {
        SpringApplication.run(MesSecurityApplication.class, args);
    }
}
