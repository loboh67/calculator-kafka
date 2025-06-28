package com.wit.rest.controller;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.wit.calculator.model.CalculationRequest;
import com.wit.calculator.model.CalculationResult;
import com.wit.rest.kafka.KafkaCalculationClient;
import com.wit.rest.kafka.ResultListener;

@RestController
public class CalculatorController {

    private static final Logger log = LoggerFactory.getLogger(CalculatorController.class);
    
    private final KafkaCalculationClient client;
    private final ResultListener resultListener;

    public CalculatorController(KafkaCalculationClient client, ResultListener resultListener) {
        this.client = client;
        this.resultListener = resultListener;
    }

    @GetMapping("/{operation}")
    public ResponseEntity<?> calculate(@RequestParam BigDecimal a,
                                       @RequestParam BigDecimal b,
                                       @PathVariable String operation) throws InterruptedException {

        String id = UUID.randomUUID().toString();
        String requestId = MDC.get("requestId");

        log.info("Received request [{}] - Operation: {}, a: {}, b: {}", requestId, operation, a, b);

        CalculationRequest request = new CalculationRequest();
        request.setId(id);
        request.setA(a);
        request.setB(b);
        request.setOperation(operation);
        request.setRequestId(requestId);

        client.send(request);
        log.info("Sent Kafka message with id {}", id);

        try {
            CalculationResult result = resultListener
                .waitForResult(id)
                .get(5, TimeUnit.SECONDS); // timeout
            
            if (result.getError() != null) {
                log.warn("Calculation [{}] returned error: {}", id, result.getError());
                return ResponseEntity.badRequest().body(Map.of("error", result.getError()));
            } else {
                log.info("Calculation [{}] result: {}", id, result.getResult());
                return ResponseEntity.ok(Map.of("result", result.getResult()));
            }
        } catch (TimeoutException e) {
            log.error("Calculation [{}] timed out", id);
            return ResponseEntity.status(504).body(Map.of("error", "Calculation timed out"));
        } catch (Exception e) {
            log.error("Unexpected error processing calculation [{}]: {}", id, e.getMessage());
            return ResponseEntity.status(500).body(Map.of("error", "Unexpected error"));
        }
    }

}
