package com.example.ordersaga.activities;

import com.example.ordersaga.model.*;
import com.example.ordersaga.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Component
public class OrderActivitiesImpl implements OrderActivities {
    
    private static final Logger log = LoggerFactory.getLogger(OrderActivitiesImpl.class);
    
    private final OrderRepository orderRepository;
    private final Random random = new Random();
    
    public OrderActivitiesImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @Override
    public void createOrder(OrderRequest orderRequest) {
        log.info("Creating order: {}", orderRequest.getOrderId());
        
        Order order = new Order();
        order.setOrderId(orderRequest.getOrderId());
        order.setCustomerId(orderRequest.getCustomerId());
        order.setTotalAmount(orderRequest.getTotalAmount());
        order.setStatus(OrderStatus.PENDING);
        
        orderRequest.getItems().forEach(itemRequest -> {
            OrderItem item = new OrderItem();
            item.setProductId(itemRequest.getProductId());
            item.setProductName(itemRequest.getProductName());
            item.setQuantity(itemRequest.getQuantity());
            item.setUnitPrice(itemRequest.getUnitPrice());
            item.setTotalPrice(itemRequest.getUnitPrice().multiply(new BigDecimal(itemRequest.getQuantity())));
            order.addItem(item);
        });
        
        orderRepository.save(order);
        log.info("Order created successfully: {}", orderRequest.getOrderId());
    }
    
    @Override
    public PaymentResult authorizePayment(OrderRequest orderRequest) {
        log.info("Authorizing payment for order: {}", orderRequest.getOrderId());
        
        // Simulate payment processing
        simulateDelay(1000, 2000);
        
        // Simulate occasional payment failures (10% chance)
        if (random.nextInt(100) < 10) {
            log.warn("Payment authorization failed for order: {}", orderRequest.getOrderId());
            return new PaymentResult(false, null, null, "Insufficient funds");
        }
        
        String paymentId = "PAY-" + UUID.randomUUID().toString().substring(0, 8);
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8);
        
        Order order = orderRepository.findByOrderId(orderRequest.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setPaymentId(paymentId);
        order.setStatus(OrderStatus.PAYMENT_AUTHORIZED);
        orderRepository.save(order);
        
        log.info("Payment authorized successfully: {} for order: {}", paymentId, orderRequest.getOrderId());
        
        return new PaymentResult(true, paymentId, transactionId, "Payment authorized successfully");
    }
    
    @Override
    public void compensatePayment(String orderId, String paymentId) {
        log.info("Compensating payment: {} for order: {}", paymentId, orderId);
        
        simulateDelay(500, 1000);
        
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.COMPENSATING);
        orderRepository.save(order);
        
        log.info("Payment compensation completed for order: {}", orderId);
    }
    
    @Override
    public InventoryResult reserveInventory(OrderRequest orderRequest) {
        log.info("Reserving inventory for order: {}", orderRequest.getOrderId());
        
        simulateDelay(1500, 2500);
        
        // Simulate occasional inventory failures (5% chance)
        if (random.nextInt(100) < 5) {
            log.warn("Inventory reservation failed for order: {}", orderRequest.getOrderId());
            return new InventoryResult(false, null, "Insufficient inventory");
        }
        
        String reservationId = "RES-" + UUID.randomUUID().toString().substring(0, 8);
        
        Order order = orderRepository.findByOrderId(orderRequest.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setReservationId(reservationId);
        order.setStatus(OrderStatus.INVENTORY_RESERVED);
        orderRepository.save(order);
        
        log.info("Inventory reserved successfully: {} for order: {}", reservationId, orderRequest.getOrderId());
        
        return new InventoryResult(true, reservationId, "Inventory reserved successfully");
    }
    
    @Override
    public void compensateInventory(String orderId, String reservationId) {
        log.info("Compensating inventory reservation: {} for order: {}", reservationId, orderId);
        
        simulateDelay(500, 1000);
        
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.COMPENSATING);
        orderRepository.save(order);
        
        log.info("Inventory compensation completed for order: {}", orderId);
    }
    
    @Override
    public FraudCheckResult performFraudCheck(OrderRequest orderRequest) {
        log.info("Performing fraud check for order: {}", orderRequest.getOrderId());
        
        simulateDelay(2000, 3000);
        
        // Calculate risk score (0-100)
        double riskScore = random.nextDouble() * 100;
        
        // Simulate occasional fraud detection (3% chance)
        boolean passed = riskScore < 85.0;
        
        if (!passed) {
            log.warn("Fraud check failed for order: {} with risk score: {}", orderRequest.getOrderId(), riskScore);
            return new FraudCheckResult(false, riskScore, "High fraud risk detected");
        }
        
        Order order = orderRepository.findByOrderId(orderRequest.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.FRAUD_CHECK_PASSED);
        orderRepository.save(order);
        
        log.info("Fraud check passed for order: {} with risk score: {}", orderRequest.getOrderId(), riskScore);
        
        return new FraudCheckResult(true, riskScore, "Fraud check passed");
    }
    
    @Override
    public void confirmOrder(String orderId) {
        log.info("Confirming order: {}", orderId);
        
        simulateDelay(500, 1000);
        
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.CONFIRMED);
        orderRepository.save(order);
        
        log.info("Order confirmed: {}", orderId);
    }
    
    @Override
    public ShippingResult arrangeShipping(OrderRequest orderRequest) {
        log.info("Arranging shipping for order: {}", orderRequest.getOrderId());
        
        simulateDelay(1000, 2000);
        
        // Simulate occasional shipping failures (2% chance)
        if (random.nextInt(100) < 2) {
            log.warn("Shipping arrangement failed for order: {}", orderRequest.getOrderId());
            return new ShippingResult(false, null, null, null, null, "Shipping carrier unavailable");
        }
        
        String shippingId = "SHIP-" + UUID.randomUUID().toString().substring(0, 8);
        String trackingNumber = "TRK" + System.currentTimeMillis();
        String[] carriers = {"FedEx", "UPS", "DHL", "USPS"};
        String carrier = carriers[random.nextInt(carriers.length)];
        
        Order order = orderRepository.findByOrderId(orderRequest.getOrderId())
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setShippingId(shippingId);
        order.setTrackingNumber(trackingNumber);
        order.setStatus(OrderStatus.SHIPPING_ARRANGED);
        orderRepository.save(order);
        
        log.info("Shipping arranged successfully: {} for order: {}", shippingId, orderRequest.getOrderId());
        
        return new ShippingResult(true, shippingId, trackingNumber, carrier, "3-5 business days", "Shipping arranged successfully");
    }
    
    @Override
    public void compensateShipping(String orderId, String shippingId) {
        log.info("Compensating shipping: {} for order: {}", shippingId, orderId);
        
        simulateDelay(500, 1000);
        
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.COMPENSATING);
        orderRepository.save(order);
        
        log.info("Shipping compensation completed for order: {}", orderId);
    }
    
    @Override
    public void updateDeliveryTracking(String orderId, String trackingNumber) {
        log.info("Updating delivery tracking for order: {} with tracking: {}", orderId, trackingNumber);
        
        simulateDelay(1000, 1500);
        
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.IN_DELIVERY);
        orderRepository.save(order);
        
        log.info("Delivery tracking updated for order: {}", orderId);
    }
    
    @Override
    public void completeOrder(String orderId) {
        log.info("Completing order: {}", orderId);
        
        simulateDelay(500, 1000);
        
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.COMPLETED);
        order.setCompletedAt(LocalDateTime.now());
        orderRepository.save(order);
        
        log.info("Order completed: {}", orderId);
    }
    
    @Override
    public void markOrderAsFailed(String orderId, String reason) {
        log.info("Marking order as failed: {} with reason: {}", orderId, reason);
        
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(OrderStatus.FAILED);
        order.setFailureReason(reason);
        orderRepository.save(order);
        
        log.info("Order marked as failed: {}", orderId);
    }
    
    @Override
    public void updateOrderStatus(String orderId, OrderStatus status) {
        log.info("Updating order status: {} to {}", orderId, status);
        
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        orderRepository.save(order);
    }
    
    private void simulateDelay(int minMillis, int maxMillis) {
        try {
            int delay = minMillis + random.nextInt(maxMillis - minMillis);
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
