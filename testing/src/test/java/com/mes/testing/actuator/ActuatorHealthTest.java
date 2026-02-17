package com.mes.testing.actuator;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DisplayName("Actuator 健康檢查測試")
class ActuatorHealthTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    @DisplayName("健康檢查應回傳 UP 狀態")
    void health_shouldReturnUp() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/actuator/health", Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("status");
        assertThat(response.getBody().get("status")).isEqualTo("UP");
    }

    @Test
    @DisplayName("健康檢查應包含元件狀態")
    void health_shouldIncludeComponents() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/actuator/health", Map.class);
        
        Map<String, Object> body = response.getBody();
        assertThat(body).containsKey("components");
    }

    @Test
    @DisplayName("自訂端點 /actuator/appinfo 應該正常運作")
    void customEndpoint_shouldReturnAppInfo() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/actuator/appinfo", Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("applicationName");
        assertThat(response.getBody()).containsKey("jvmVersion");
    }

    @Test
    @DisplayName("info 端點應該可存取")
    void infoEndpoint_shouldBeAccessible() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/actuator/info", Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    @DisplayName("beans 端點應該回傳 Spring Bean 清單")
    void beansEndpoint_shouldReturnBeanList() {
        ResponseEntity<Map> response = restTemplate.getForEntity("/actuator/beans", Map.class);
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).containsKey("contexts");
    }
}
