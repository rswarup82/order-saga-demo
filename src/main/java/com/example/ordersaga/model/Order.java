package com.example.ordersaga.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String orderId;
    
    private String customerId;
    
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "order", orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();
    
    private BigDecimal totalAmount;
    
    private String paymentId;
    private String reservationId;
    private String shippingId;
    private String trackingNumber;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    
    @Column(length = 1000)
    private String failureReason;
    
    public Order() {
    }
    
    public Order(Long id, String orderId, String customerId, OrderStatus status, 
                 List<OrderItem> items, BigDecimal totalAmount, String paymentId, 
                 String reservationId, String shippingId, String trackingNumber, 
                 LocalDateTime createdAt, LocalDateTime updatedAt, LocalDateTime completedAt, 
                 String failureReason) {
        this.id = id;
        this.orderId = orderId;
        this.customerId = customerId;
        this.status = status;
        this.items = items;
        this.totalAmount = totalAmount;
        this.paymentId = paymentId;
        this.reservationId = reservationId;
        this.shippingId = shippingId;
        this.trackingNumber = trackingNumber;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.completedAt = completedAt;
        this.failureReason = failureReason;
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = OrderStatus.PENDING;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public void addItem(OrderItem item) {
        items.add(item);
        item.setOrder(this);
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getShippingId() {
        return shippingId;
    }

    public void setShippingId(String shippingId) {
        this.shippingId = shippingId;
    }

    public String getTrackingNumber() {
        return trackingNumber;
    }

    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id) && Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, orderId);
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", orderId='" + orderId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", status=" + status +
                ", totalAmount=" + totalAmount +
                ", paymentId='" + paymentId + '\'' +
                ", reservationId='" + reservationId + '\'' +
                ", shippingId='" + shippingId + '\'' +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", completedAt=" + completedAt +
                ", failureReason='" + failureReason + '\'' +
                '}';
    }
}
