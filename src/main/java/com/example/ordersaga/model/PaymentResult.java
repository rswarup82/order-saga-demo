package com.example.ordersaga.model;

import java.io.Serializable;
import java.util.Objects;

public class PaymentResult implements Serializable {
    private boolean success;
    private String paymentId;
    private String transactionId;
    private String message;
    
    public PaymentResult() {
    }
    
    public PaymentResult(boolean success, String paymentId, String transactionId, String message) {
        this.success = success;
        this.paymentId = paymentId;
        this.transactionId = transactionId;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PaymentResult that = (PaymentResult) o;
        return success == that.success && Objects.equals(paymentId, that.paymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, paymentId);
    }

    @Override
    public String toString() {
        return "PaymentResult{" +
                "success=" + success +
                ", paymentId='" + paymentId + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
