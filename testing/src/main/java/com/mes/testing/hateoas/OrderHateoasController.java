package com.mes.testing.hateoas;

import com.mes.testing.application.OrderService;
import com.mes.testing.domain.Order;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/api/v1/hateoas/orders")
public class OrderHateoasController {

    private final OrderService orderService;
    private final OrderAssembler orderAssembler;

    public OrderHateoasController(OrderService orderService, OrderAssembler orderAssembler) {
        this.orderService = orderService;
        this.orderAssembler = orderAssembler;
    }

    @GetMapping
    public ResponseEntity<RepresentationModel<?>> getAllOrders(@RequestParam(required = false) String status) {
        List<EntityModel<Order>> orders;
        
        if (status != null) {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            orders = orderService.getOrdersByStatus(orderStatus).stream()
                    .map(orderAssembler::toModel)
                    .collect(Collectors.toList());
        } else {
            orders = orderService.getAllOrders().stream()
                    .map(orderAssembler::toModel)
                    .collect(Collectors.toList());
        }
        
        RepresentationModel<?> root = orderAssembler.getLinks();
        root.add(Link.of("/api/v1/hateoas/orders", "orders"));
        
        return ResponseEntity.ok(root);
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<EntityModel<Order>> getOrder(@PathVariable String orderId) {
        try {
            Order order = orderService.getOrder(orderId);
            return ResponseEntity.ok(orderAssembler.toModel(order));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<EntityModel<Order>> createOrder(@RequestBody Order order) {
        Order created = orderService.createOrder(order.getOrderId(), order.getCustomerName(), order.getAmount());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderAssembler.toModel(created));
    }

    @PostMapping("/{orderId}/confirm")
    public ResponseEntity<EntityModel<Order>> confirmOrder(@PathVariable String orderId) {
        try {
            Order order = orderService.confirmOrder(orderId);
            return ResponseEntity.ok(orderAssembler.toModel(order));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<EntityModel<Order>> cancelOrder(@PathVariable String orderId) {
        try {
            Order order = orderService.cancelOrder(orderId);
            return ResponseEntity.ok(orderAssembler.toModel(order));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/{orderId}/ship")
    public ResponseEntity<EntityModel<Order>> shipOrder(@PathVariable String orderId) {
        try {
            Order order = orderService.shipOrder(orderId);
            return ResponseEntity.ok(orderAssembler.toModel(order));
        } catch (IllegalStateException | IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
