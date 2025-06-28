package com.wit.calculator.kafka;

import com.wit.calculator.model.CalculationRequest;
import com.wit.calculator.model.CalculationResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculationListenerTest {

    @Mock
    private KafkaTemplate<String, CalculationResult> kafkaTemplate;

    private CalculationListener listener;

    @Test
    void testValidSum() {
        listener = new CalculationListener("results", kafkaTemplate);

        CalculationRequest request = new CalculationRequest();
        request.setId("abc");
        request.setA(new BigDecimal("3"));
        request.setB(new BigDecimal("2"));
        request.setOperation("sum");

        listener.listen(request);

        // Verifica se kafkaTemplate.send foi chamado com os argumentos corretos
        verify(kafkaTemplate).send(eq("results"), eq("abc"), argThat(result ->
            result.getResult().equals(new BigDecimal("5")) &&
            result.getError() == null
        ));
    }

    @Test
    void testValidSubtract() {
        listener = new CalculationListener("results", kafkaTemplate);

        CalculationRequest request = new CalculationRequest();
        request.setId("abc");
        request.setA(new BigDecimal("7"));
        request.setB(new BigDecimal("5"));
        request.setOperation("subtract");

        listener.listen(request);

        // Verifica se kafkaTemplate.send foi chamado com os argumentos corretos
        verify(kafkaTemplate).send(eq("results"), eq("abc"), argThat(result ->
            result.getResult().equals(new BigDecimal("2")) &&
            result.getError() == null
        ));
    }

    @Test
    void testValidMultiply() {
        listener = new CalculationListener("results", kafkaTemplate);

        CalculationRequest request = new CalculationRequest();
        request.setId("abc");
        request.setA(new BigDecimal("10"));
        request.setB(new BigDecimal("3"));
        request.setOperation("multiply");

        listener.listen(request);

        // Verifica se kafkaTemplate.send foi chamado com os argumentos corretos
        verify(kafkaTemplate).send(eq("results"), eq("abc"), argThat(result ->
            result.getResult().equals(new BigDecimal("30")) &&
            result.getError() == null
        ));
    }

    @Test
    void testExactDivide() {
        listener = new CalculationListener("results", kafkaTemplate);

        CalculationRequest request = new CalculationRequest();
        request.setId("abc");
        request.setA(new BigDecimal("27"));
        request.setB(new BigDecimal("3"));
        request.setOperation("divide");

        listener.listen(request);

        // Verifica se kafkaTemplate.send foi chamado com os argumentos corretos
        verify(kafkaTemplate).send(eq("results"), eq("abc"), argThat(result -> 
            result.getResult().equals(new BigDecimal("9")) &&
            result.getError() == null
        ));
    }

    @Test
    void testDivideOneDecimalPlace() {
        listener = new CalculationListener("results", kafkaTemplate);

        CalculationRequest request = new CalculationRequest();
        request.setId("abc");
        request.setA(new BigDecimal("15"));
        request.setB(new BigDecimal("2"));
        request.setOperation("divide");

        listener.listen(request);

        // Verifica se kafkaTemplate.send foi chamado com os argumentos corretos
        verify(kafkaTemplate).send(eq("results"), eq("abc"), argThat(result -> 
            result.getResult().equals(new BigDecimal("7.5")) &&
            result.getError() == null
        ));
    }

    @Test
    void testDivideTwoDecimalPlaces() {
        listener = new CalculationListener("results", kafkaTemplate);

        CalculationRequest request = new CalculationRequest();
        request.setId("abc");
        request.setA(new BigDecimal("2.5"));
        request.setB(new BigDecimal("2"));
        request.setOperation("divide");

        listener.listen(request);

        // Verifica se kafkaTemplate.send foi chamado com os argumentos corretos
        verify(kafkaTemplate).send(eq("results"), eq("abc"), argThat(result -> 
            result.getResult().equals(new BigDecimal("1.25")) &&
            result.getError() == null
        ));
    }

     @Test
    void testDivideMultipleDecimalPlaces() {
        listener = new CalculationListener("results", kafkaTemplate);

        CalculationRequest request = new CalculationRequest();
        request.setId("abc");
        request.setA(new BigDecimal("10"));
        request.setB(new BigDecimal("3"));
        request.setOperation("divide");

        listener.listen(request);

        // Verifica se kafkaTemplate.send foi chamado com os argumentos corretos
        verify(kafkaTemplate).send(eq("results"), eq("abc"), argThat(result -> 
            result.getResult().equals(new BigDecimal("3.3333333333")) &&
            result.getError() == null
        ));
    }

    @Test
    void testDivideByZero() {
        listener = new CalculationListener("results", kafkaTemplate);

        CalculationRequest request = new CalculationRequest();
        request.setId("abc");
        request.setA(new BigDecimal("10"));
        request.setB(BigDecimal.ZERO);
        request.setOperation("divide");

        listener.listen(request);

        verify(kafkaTemplate).send(eq("results"), eq("abc"), argThat(result -> {
            if (result == null) return false;
            return result.getResult() == null &&
                result.getError() != null &&
                result.getError().equals("Cannot divid by zero");
        }));
    }

    @Test
    void testInvalidOperation() {
        listener = new CalculationListener("results", kafkaTemplate);

        CalculationRequest request = new CalculationRequest();
        request.setId("abc");
        request.setA(new BigDecimal("10"));
        request.setB(new BigDecimal("5"));
        request.setOperation("sqrt");

        listener.listen(request);

        verify(kafkaTemplate).send(eq("results"), eq("abc"), argThat(result -> {
            if (result == null) return false;
            return result.getResult() == null &&
                   result.getError() != null &&
                   result.getError().equals("Invalid operation: sqrt");
        }));
    }
}