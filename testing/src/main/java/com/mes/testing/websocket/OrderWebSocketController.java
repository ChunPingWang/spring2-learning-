package com.mes.testing.websocket;

import com.mes.testing.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
public class OrderWebSocketController {

    private static final Logger log = LoggerFactory.getLogger(OrderWebSocketController.class);
    private final SimpMessagingTemplate messagingTemplate;

    public OrderWebSocketController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/orders/{orderId}/update")
    @SendTo("/topic/orders")
    public Order handleOrderUpdate(@DestinationVariable String orderId, Order order) {
        log.info("Received order update via WebSocket: {}", orderId);
        return order;
    }

    @MessageMapping("/orders/broadcast")
    public void broadcastOrder(Order order) {
        log.info("Broadcasting order: {}", order.getOrderId());
        messagingTemplate.convertAndSend("/topic/orders", order);
    }

    public void notifyOrderCreated(Order order) {
        messagingTemplate.convertAndSend("/topic/orders/created", order);
    }

    public void notifyOrderStatusChanged(String orderId, String status) {
        messagingTemplate.convertAndSend("/topic/orders/" + orderId + "/status", 
                java.util.Map.of("orderId", orderId, "status", status));
    }
}
