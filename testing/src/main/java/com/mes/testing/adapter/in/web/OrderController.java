package com.mes.testing.adapter.in.web;

import com.mes.testing.application.OrderService;
import com.mes.testing.domain.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request.getOrderId(), request.getCustomerName(), request.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderDto.fromDomain(order));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<OrderDto> getOrder(@PathVariable String orderId) {
        try {
            Order order = orderService.getOrder(orderId);
            return ResponseEntity.ok(OrderDto.fromDomain(order));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping
    public ResponseEntity<List<OrderDto>> getAllOrders(@RequestParam(required = false) String status) {
        List<OrderDto> orders;
        if (status != null) {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            orders = orderService.getOrdersByStatus(orderStatus).stream()
                    .map(OrderDto::fromDomain)
                    .collect(Collectors.toList());
        } else {
            orders = orderService.getAllOrders().stream()
                    .map(OrderDto::fromDomain)
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(orders);
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<OrderDto> confirmOrder(@PathVariable String orderId) {
        try {
            Order order = orderService.confirmOrder(orderId);
            return ResponseEntity.ok(OrderDto.fromDomain(order));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<OrderDto> cancelOrder(@PathVariable String orderId) {
        try {
            Order order = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(OrderDto.fromDomain(order));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/stats/revenue")
    public ResponseEntity<Map<String, Double>> getRevenue() {
        Double revenue = orderService.calculateTotalRevenue();
        return ResponseEntity.ok(Map.of("totalRevenue", revenue));
    }

    public static class CreateOrderRequest {
        @NotBlank
        private String orderId;
        @NotBlank
        private String customerName;
        @NotNull
        @Positive
        private Double amount;

        public String getOrderId() {
            return orderId;
        }

        public void setOrderId(String orderId) {
            this.orderId = orderId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public void setCustomerName(String customerName) {
            this.customerName = customerName;
        }

        public Double getAmount() {
            return amount;
        }

        public void setAmount(Double amount) {
            this.amount = amount;
        }
    }

    public static class OrderDto {
        private String orderId;
        private String customerName;
        private Double amount;
        private String status;
        private String createdAt;

        public static OrderDto fromDomain(Order order) {
            OrderDto dto = new OrderDto();
            dto.orderId = order.getOrderId();
            dto.customerName = order.getCustomerName();
            dto.amount = order.getAmount();
            dto.status = order.getStatus().name();
            dto.createdAt = order.getCreatedAt().toString();
            return dto;
        }

        public String getOrderId() {
            return orderId;
        }

        public String getCustomerName() {
            return customerName;
        }

        public Double getAmount() {
            return amount;
        }

        public String getStatus() {
            return status;
        }

        public String getCreatedAt() {
            return createdAt;
        }
    }
}
