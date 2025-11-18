package com.develop.ecommerce.orchestrator;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventHandler {

    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "order-events")
    public void handleOrderEvents(String message) throws JsonProcessingException {

        OrderCreatedEvent event = mapper.readValue(message, OrderCreatedEvent.class);

        log.info("Orchestrator received order event {}", event.getOrderId());

        PaymentProcessCommand cmd = new PaymentProcessCommand(
                event.getOrderId(),
                event.getAmount()
        );

        kafka.send("payment-commands", mapper.writeValueAsString(cmd));
    }
}

