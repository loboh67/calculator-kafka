package com.wit.calculator.model;

import java.math.BigDecimal;

public class CalculationRequest {
    private String id;
    private BigDecimal a;
    private BigDecimal b;
    private String operation;
    private String requestId;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public BigDecimal getA() {
        return a;
    }
    public void setA(BigDecimal a) {
        this.a = a;
    }
    public BigDecimal getB() {
        return b;
    }
    public void setB(BigDecimal b) {
        this.b = b;
    }
    public String getOperation() {
        return operation;
    }
    public void setOperation(String operation) {
        this.operation = operation;
    }
    public String getRequestId() {
        return requestId;
    }
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
