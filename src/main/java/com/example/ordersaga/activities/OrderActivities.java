package com.example.ordersaga.activities;

import com.example.ordersaga.model.*;
import io.temporal.activity.ActivityInterface;
import io.temporal.activity.ActivityMethod;

@ActivityInterface
public interface OrderActivities {
    
    @ActivityMethod
    void createOrder(OrderRequest orderRequest);
    
    @ActivityMethod
    PaymentResult authorizePayment(OrderRequest orderRequest);
    
    @ActivityMethod
    void compensatePayment(String orderId, String paymentId);
    
    @ActivityMethod
    InventoryResult reserveInventory(OrderRequest orderRequest);
    
    @ActivityMethod
    void compensateInventory(String orderId, String reservationId);
    
    @ActivityMethod
    FraudCheckResult performFraudCheck(OrderRequest orderRequest);
    
    @ActivityMethod
    void confirmOrder(String orderId);
    
    @ActivityMethod
    ShippingResult arrangeShipping(OrderRequest orderRequest);
    
    @ActivityMethod
    void compensateShipping(String orderId, String shippingId);
    
    @ActivityMethod
    void updateDeliveryTracking(String orderId, String trackingNumber);
    
    @ActivityMethod
    void completeOrder(String orderId);
    
    @ActivityMethod
    void markOrderAsFailed(String orderId, String reason);
    
    @ActivityMethod
    void updateOrderStatus(String orderId, OrderStatus status);
}
