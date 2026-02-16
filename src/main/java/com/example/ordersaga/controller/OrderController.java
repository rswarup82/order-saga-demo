package com.example.ordersaga.controller;

import com.example.ordersaga.model.Order;
import com.example.ordersaga.model.OrderRequest;
import com.example.ordersaga.model.OrderStatus;
import com.example.ordersaga.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;
    
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }
    
    @PostMapping
    public ResponseEntity<Map<String, String>> createOrder(@RequestBody OrderRequest orderRequest) {
        String orderId = orderService.createOrder(orderRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                        "orderId", orderId,
                        "message", "Order workflow started successfully",
                        "status", "PROCESSING"
                ));
    }
    
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrder(@PathVariable String orderId) {
        Order order = orderService.getOrder(orderId);
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Order>> getOrdersByCustomer(@PathVariable String customerId) {
        List<Order> orders = orderService.getOrdersByCustomer(customerId);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
    
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
}
