package com.mes.redis.dashboard.application.command;

import com.mes.common.cqrs.CommandHandler;
import com.mes.redis.dashboard.domain.port.out.CachePort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * [CQRS Pattern: Command Handler - 處理清除快取命令]
 * [SOLID: SRP - 只負責清除指定的快取鍵]
 * [SOLID: DIP - 依賴 CachePort 抽象，不依賴具體 Redis 實作]
 *
 * 透過 CachePort 清除指定的快取鍵。
 * 展示領域層如何透過 Port 操作快取。
 */
@Component
public class InvalidateCacheCommandHandler implements CommandHandler<InvalidateCacheCommand, Void> {

    private static final Logger log = LoggerFactory.getLogger(InvalidateCacheCommandHandler.class);

    private final CachePort cachePort;

    public InvalidateCacheCommandHandler(CachePort cachePort) {
        this.cachePort = cachePort;
    }

    @Override
    public Void handle(InvalidateCacheCommand command) {
        log.debug("Handling InvalidateCacheCommand for key={}", command.getCacheKey());
        cachePort.evict(command.getCacheKey());
        log.info("Cache invalidated for key={}", command.getCacheKey());
        return null;
    }

    @Override
    public Class<InvalidateCacheCommand> getCommandType() {
        return InvalidateCacheCommand.class;
    }
}
