package com.mes.cloud.material.adapter.in.web;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * [Spring Cloud Alibaba: 健康檢查端點]
 * [SOLID: SRP - 只負責回應健康檢查請求]
 *
 * <h2>教學重點：Nacos 健康檢查</h2>
 * <p>
 * Nacos 服務發現會定期對已註冊的服務進行健康檢查。
 * 預設使用心跳機制（客戶端主動上報），也可以配合自定義的健康檢查端點。
 * </p>
 * <p>
 * 此控制器提供一個簡單的 HTTP 健康檢查端點，
 * 可被 Nacos、負載均衡器或 Kubernetes 探針使用。
 * </p>
 */
@RestController
@RequestMapping("/api/v1/health")
public class HealthCheckController {

    /**
     * 健康檢查端點。
     *
     * @return 健康狀態
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Map<String, Object>>> healthCheck() {
        Map<String, Object> healthInfo = new HashMap<String, Object>();
        healthInfo.put("status", "UP");
        healthInfo.put("service", "mes-cloud-alibaba");
        healthInfo.put("module", "material-management");
        healthInfo.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(ApiResponse.success(healthInfo));
    }
}
