package com.example.ordersaga.model;

public enum OrderStatus {
    PENDING,
    PAYMENT_AUTHORIZED,
    INVENTORY_RESERVED,
    FRAUD_CHECK_PASSED,
    CONFIRMED,
    SHIPPING_ARRANGED,
    IN_DELIVERY,
    COMPLETED,
    FAILED,
    COMPENSATING,
    COMPENSATED
}
