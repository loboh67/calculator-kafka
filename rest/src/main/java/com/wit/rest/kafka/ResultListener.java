package com.wit.rest.kafka;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.wit.calculator.model.CalculationResult;

@Service
public class ResultListener {
    
    private final Map<String, CompletableFuture<CalculationResult>> futureMap = new ConcurrentHashMap<>();
    
    @KafkaListener(topics = "${spring.kafka.topics.results}", groupId = "${spring.kafka.consumer.group-id}")
    public void consume(CalculationResult result) {
        CompletableFuture<CalculationResult> future = futureMap.remove(result.getId());
        if (future != null) {
            future.complete(result);
        }
    }

    public CompletableFuture<CalculationResult> waitForResult(String id) {
        CompletableFuture<CalculationResult> future = new CompletableFuture<>();
        futureMap.put(id, future);
        return future;
    }
}
