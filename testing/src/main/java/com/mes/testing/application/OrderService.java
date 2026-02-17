package com.mes.testing.application;

import com.mes.testing.domain.Order;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    public Order createOrder(String orderId, String customerName, Double amount) {
        if (orders.containsKey(orderId)) {
            throw new IllegalArgumentException("Order already exists: " + orderId);
        }
        Order order = new Order(orderId, customerName, amount);
        orders.put(orderId, order);
        return order;
    }

    public Order getOrder(String orderId) {
        Order order = orders.get(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }
        return order;
    }

    public Order confirmOrder(String orderId) {
        Order order = getOrder(orderId);
        order.confirm();
        return order;
    }

    public Order cancelOrder(String orderId) {
        Order order = getOrder(orderId);
        order.cancel();
        return order;
    }

    public Order shipOrder(String orderId) {
        Order order = getOrder(orderId);
        order.ship();
        return order;
    }

    public List<Order> getAllOrders() {
        return new ArrayList<>(orders.values());
    }

    public List<Order> getOrdersByStatus(Order.OrderStatus status) {
        return orders.values().stream()
                .filter(o -> o.getStatus() == status)
                .collect(Collectors.toList());
    }

    public Double calculateTotalRevenue() {
        return orders.values().stream()
                .filter(o -> o.getStatus() != Order.OrderStatus.CANCELLED)
                .mapToDouble(Order::getAmount)
                .sum();
    }
}
