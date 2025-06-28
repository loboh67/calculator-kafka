package com.wit.calculator.model;

import java.math.BigDecimal;

public class CalculationResult {
    private String id;
    private BigDecimal result;
    private String error;

    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public BigDecimal getResult() {
        return result;
    }
    public void setResult(BigDecimal result) {
        this.result = result;
    }
    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }
}
