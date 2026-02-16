package com.mes.cloud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * [Spring Boot: 應用程式上下文載入測試]
 * [Spring Cloud Alibaba: 禁用 Nacos 與 Sentinel 進行離線測試]
 *
 * 驗證 Spring 應用程式上下文可以在不連接 Nacos/Sentinel 的情況下正確載入。
 * 透過 test properties 禁用所有外部服務的自動配置。
 */
@DisplayName("MesCloudAlibabaApplication 應用程式啟動測試")
@SpringBootTest(properties = {
        "spring.cloud.nacos.discovery.enabled=false",
        "spring.cloud.nacos.config.enabled=false",
        "spring.cloud.sentinel.enabled=false",
        "spring.cloud.nacos.config.import-check.enabled=false"
})
class MesCloudAlibabaApplicationTest {

    @Test
    @DisplayName("應用程式上下文應成功載入（Nacos 與 Sentinel 已禁用）")
    void contextLoads() {
        // 如果 Spring 上下文載入失敗，此測試會自動失敗
    }
}
