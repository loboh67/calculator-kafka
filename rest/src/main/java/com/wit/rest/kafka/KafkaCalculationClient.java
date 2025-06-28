package com.wit.rest.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.wit.calculator.model.CalculationRequest;

@Service
public class KafkaCalculationClient {

    @Value("${spring.kafka.topics.calculation}")
    private String calculationTopic;

    private final KafkaTemplate<String, CalculationRequest> kafkaTemplate;

    public KafkaCalculationClient(KafkaTemplate<String, CalculationRequest> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(CalculationRequest request) {
        kafkaTemplate.send(calculationTopic, request.getId(), request);
    } 
}
