package com.wit.rest.controller;

import com.wit.calculator.model.CalculationRequest;
import com.wit.calculator.model.CalculationResult;
import com.wit.rest.kafka.KafkaCalculationClient;
import com.wit.rest.kafka.ResultListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


public class CalculatorControllerTest {
    
    private KafkaCalculationClient client;
    private ResultListener resultListener;
    private CalculatorController controller;

    @BeforeEach
    void setup() {
        client = mock(KafkaCalculationClient.class);
        resultListener = mock(ResultListener.class);
        controller = new CalculatorController(client, resultListener);
    }

    @Test
    void testSuccessfulSum() throws Exception {
        BigDecimal a = new BigDecimal("3");
        BigDecimal b = new BigDecimal("2");

        CalculationResult result = new CalculationResult();
        result.setResult(new BigDecimal("5"));

        when(resultListener.waitForResult(anyString()))
            .thenReturn(CompletableFuture.completedFuture(result));

        ResponseEntity<?> response = controller.calculate(a, b, "sum");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(Map.of("result", new BigDecimal("5")));
        verify(client).send(any(CalculationRequest.class));
    }

    @Test
    void testCalculationError() throws Exception {
        CalculationResult result = new CalculationResult();
        result.setError("Cannot divide by zero");

        when(resultListener.waitForResult(anyString()))
            .thenReturn(CompletableFuture.completedFuture(result));

        ResponseEntity<?> response = controller.calculate(BigDecimal.TEN, BigDecimal.ZERO, "divide");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isEqualTo(Map.of("error", "Cannot divide by zero"));
    }

    @SuppressWarnings("null")
    @Test
    void testTimeout() throws Exception {
        when(resultListener.waitForResult(anyString()))
            .thenReturn(CompletableFuture.supplyAsync(() -> {
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    // ignore
                } return null;
            }));

        ResponseEntity<?> response = controller.calculate(BigDecimal.ONE, BigDecimal.ONE, "sum");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
        assertThat(((Map<?, ?>) response.getBody()).get("error")).isEqualTo("Calculation timed out");
    }
}
