package com.mes.security.auth.infrastructure.event;

import com.mes.common.ddd.event.DomainEvent;
import com.mes.common.ddd.event.DomainEventPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * [DDD Pattern: Domain Event Publisher - Adapter (基礎設施層實作)]
 * [SOLID: DIP - 實作領域層定義的 DomainEventPublisher 介面]
 * [SOLID: LSP - 可替換為 Kafka、RabbitMQ 等其他實作]
 * [Hexagonal Architecture: Output Adapter]
 *
 * 簡單的日誌記錄實作。在教學環境中用於觀察領域事件的發佈。
 */
@Component
public class LoggingDomainEventPublisher implements DomainEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(LoggingDomainEventPublisher.class);

    @Override
    public void publish(DomainEvent event) {
        log.info("發佈領域事件: {}", event);
    }
}
