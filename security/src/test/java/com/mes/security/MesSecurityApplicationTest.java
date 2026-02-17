package com.mes.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * [DDD Pattern: Bounded Context 整合測試]
 *
 * 驗證 Spring Boot 應用上下文能正確載入。
 */
@DisplayName("MesSecurityApplication 上下文載入測試")
@SpringBootTest
class MesSecurityApplicationTest {

    @Test
    @DisplayName("應用上下文應成功載入")
    void contextLoads() {
        // 若上下文載入失敗，此測試會自動失敗
    }
}
