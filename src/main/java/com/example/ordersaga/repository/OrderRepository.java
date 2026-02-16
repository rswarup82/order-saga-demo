package com.example.ordersaga.repository;

import com.example.ordersaga.model.Order;
import com.example.ordersaga.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByOrderId(String orderId);
    List<Order> findByCustomerId(String customerId);
    List<Order> findByStatus(OrderStatus status);
}
