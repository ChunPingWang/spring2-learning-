package com.mes.testing.reactive;

import com.mes.testing.domain.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api/v1/reactive/orders")
public class ReactiveOrderController {

    private static final Logger log = LoggerFactory.getLogger(ReactiveOrderController.class);
    private final Map<String, Order> orderStore = new ConcurrentHashMap<>();

    @GetMapping(value = "/stream", produces = "text/event-stream")
    public Flux<Order> streamOrders() {
        return Flux.interval(Duration.ofSeconds(2))
                .map(i -> {
                    Order order = new Order("ORD-" + i, "Customer-" + i, 100.0 * i);
                    log.debug("Streaming order: {}", order.getOrderId());
                    return order;
                })
                .take(10);
    }

    @GetMapping("/{orderId}")
    public Mono<Order> getOrder(@PathVariable String orderId) {
        log.info("Fetching order: {}", orderId);
        return Mono.justOrEmpty(orderStore.get(orderId))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Order not found")));
    }

    @PostMapping
    public Mono<Order> createOrder(@RequestBody Order order) {
        log.info("Creating order: {}", order.getOrderId());
        orderStore.put(order.getOrderId(), order);
        return Mono.just(order);
    }

    @GetMapping
    public Flux<Order> getAllOrders() {
        log.info("Fetching all orders");
        return Flux.fromIterable(orderStore.values());
    }

    @GetMapping("/count")
    public Mono<Map<String, Long>> getOrderCount() {
        return Mono.just(Map.of("count", (long) orderStore.size()));
    }

    @DeleteMapping("/{orderId}")
    public Mono<Void> deleteOrder(@PathVariable String orderId) {
        log.info("Deleting order: {}", orderId);
        orderStore.remove(orderId);
        return Mono.empty();
    }
}
