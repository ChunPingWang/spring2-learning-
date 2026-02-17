package com.mes.bus.adapter.in.web;

import com.mes.bus.service.ConfigService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/config")
public class ConfigController {

    private final ConfigService configService;

    public ConfigController(ConfigService configService) {
        this.configService = configService;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> getConfig() {
        return ResponseEntity.ok(configService.getConfig());
    }

    @GetMapping("/value")
    public ResponseEntity<String> getConfigValue() {
        return ResponseEntity.ok(configService.getConfigValue());
    }

    @GetMapping("/message")
    public ResponseEntity<String> getMessage() {
        return ResponseEntity.ok(configService.getMessage());
    }
}
