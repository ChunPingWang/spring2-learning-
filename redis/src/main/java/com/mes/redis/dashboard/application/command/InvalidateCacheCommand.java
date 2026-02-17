package com.mes.redis.dashboard.application.command;

import com.mes.common.cqrs.Command;

/**
 * [CQRS Pattern: Command - 清除快取]
 * [SOLID: SRP - 只負責攜帶清除快取所需的鍵]
 *
 * 用於手動清除指定的快取鍵。
 */
public class InvalidateCacheCommand implements Command {

    private final String cacheKey;

    public InvalidateCacheCommand(String cacheKey) {
        this.cacheKey = cacheKey;
    }

    public String getCacheKey() {
        return cacheKey;
    }
}
