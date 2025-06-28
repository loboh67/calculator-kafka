package com.wit.calculator.kafka;

import org.springframework.stereotype.Service;

import com.wit.calculator.model.CalculationRequest;
import com.wit.calculator.model.CalculationResult;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

@Service
public class CalculationListener {

    private static final Logger log = LoggerFactory.getLogger(CalculationListener.class);

    private final String resultsTopic; 
    private final KafkaTemplate<String, CalculationResult> kafkaTemplate;

    public CalculationListener(
        @Value("${spring.kafka.topics.results}") String resultsTopic,
        KafkaTemplate<String, CalculationResult> kafkaTemplate
        ) {
            this.resultsTopic = resultsTopic;
            this.kafkaTemplate = kafkaTemplate;
    } 

    @KafkaListener(
        topics = "${spring.kafka.topics.calculation}",
        groupId = "${spring.kafka.consumer.group-id}"
    )
    public void listen(CalculationRequest request) {

        MDC.put("requestId", request.getRequestId());

        CalculationResult response = new CalculationResult();
        response.setId(request.getId());

        try {

            log.info("Processing calculation [{}], op={}, a={}, b={}",
                request.getId(),
                request.getOperation(),
                request.getA(),
                request.getB());

            BigDecimal a = request.getA();
            BigDecimal b = request.getB();
            BigDecimal result;

            switch (request.getOperation()) {
                case "sum":
                    result = a.add(b);
                    break;
                case "subtract":
                    result = a.subtract(b);
                    break;
                case "multiply":
                    result = a.multiply(b);
                    break;
                case "divide":
                    if (b.compareTo(BigDecimal.ZERO) == 0) {
                        throw new ArithmeticException("Cannot divid by zero");
                    }
                    
                    BigDecimal[] division = a.divideAndRemainder(b);

                    if (division[1].compareTo(BigDecimal.ZERO) == 0) {
                        // Exact division
                        result = a.divide(b);
                    } else {
                        // Non-exact: returns decimal with up to 10 decimal places
                        result = a.divide(b, 10, RoundingMode.HALF_UP).stripTrailingZeros();
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Invalid operation: " + request.getOperation());
            }

            response.setResult(result);
        
        } catch (Exception e) {
            log.warn("Calculation [{}] failed: {}", request.getId(), e.getMessage());
            response.setError(e.getMessage());
        }

        kafkaTemplate.send(resultsTopic, response.getId(), response);
    }

}
