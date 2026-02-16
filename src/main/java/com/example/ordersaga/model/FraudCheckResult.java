package com.example.ordersaga.model;

import java.io.Serializable;
import java.util.Objects;

public class FraudCheckResult implements Serializable {
    private boolean passed;
    private double riskScore;
    private String message;
    
    public FraudCheckResult() {
    }
    
    public FraudCheckResult(boolean passed, double riskScore, String message) {
        this.passed = passed;
        this.riskScore = riskScore;
        this.message = message;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public double getRiskScore() {
        return riskScore;
    }

    public void setRiskScore(double riskScore) {
        this.riskScore = riskScore;
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
        FraudCheckResult that = (FraudCheckResult) o;
        return passed == that.passed && Double.compare(that.riskScore, riskScore) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(passed, riskScore);
    }

    @Override
    public String toString() {
        return "FraudCheckResult{" +
                "passed=" + passed +
                ", riskScore=" + riskScore +
                ", message='" + message + '\'' +
                '}';
    }
}
