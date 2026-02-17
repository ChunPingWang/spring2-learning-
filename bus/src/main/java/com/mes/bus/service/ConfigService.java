package com.mes.bus.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RefreshScope
public class ConfigService {

    @Value("${mes.bus.config.value:default-value}")
    private String configValue;

    @Value("${mes.bus.message:default-message}")
    private String message;

    public Map<String, Object> getConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("configValue", configValue);
        config.put("message", message);
        return config;
    }

    public String getConfigValue() {
        return configValue;
    }

    public String getMessage() {
        return message;
    }
}
