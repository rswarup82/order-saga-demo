package com.example.ordersaga.model;

import java.io.Serializable;
import java.util.Objects;

public class ShippingResult implements Serializable {
    private boolean success;
    private String shippingId;
    private String trackingNumber;
    private String carrier;
    private String estimatedDelivery;
    private String message;
    
    public ShippingResult() {
    }
    
    public ShippingResult(boolean success, String shippingId, String trackingNumber, 
                         String carrier, String estimatedDelivery, String message) {
        this.success = success;
        this.shippingId = shippingId;
        this.trackingNumber = trackingNumber;
        this.carrier = carrier;
        this.estimatedDelivery = estimatedDelivery;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public String getCarrier() {
        return carrier;
    }

    public void setCarrier(String carrier) {
        this.carrier = carrier;
    }

    public String getEstimatedDelivery() {
        return estimatedDelivery;
    }

    public void setEstimatedDelivery(String estimatedDelivery) {
        this.estimatedDelivery = estimatedDelivery;
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
        ShippingResult that = (ShippingResult) o;
        return success == that.success && Objects.equals(shippingId, that.shippingId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, shippingId);
    }

    @Override
    public String toString() {
        return "ShippingResult{" +
                "success=" + success +
                ", shippingId='" + shippingId + '\'' +
                ", trackingNumber='" + trackingNumber + '\'' +
                ", carrier='" + carrier + '\'' +
                ", estimatedDelivery='" + estimatedDelivery + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
