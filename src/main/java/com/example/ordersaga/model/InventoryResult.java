package com.example.ordersaga.model;

import java.io.Serializable;
import java.util.Objects;

public class InventoryResult implements Serializable {
    private boolean success;
    private String reservationId;
    private String message;
    
    public InventoryResult() {
    }
    
    public InventoryResult(boolean success, String reservationId, String message) {
        this.success = success;
        this.reservationId = reservationId;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
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
        InventoryResult that = (InventoryResult) o;
        return success == that.success && Objects.equals(reservationId, that.reservationId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(success, reservationId);
    }

    @Override
    public String toString() {
        return "InventoryResult{" +
                "success=" + success +
                ", reservationId='" + reservationId + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}
