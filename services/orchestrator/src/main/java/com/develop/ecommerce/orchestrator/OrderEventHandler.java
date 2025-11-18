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
    public void handleOrderEvents(String message) throws Exception {
        OrderEventEnvelope envelope = mapper.readValue(message, OrderEventEnvelope.class);

        switch (envelope.getEventType()) {
            case "PAYMENT_REQUESTED" -> handlePaymentRequested(envelope.getPayload());
            case "NOTIFICATION_REQUESTED" -> handleNotificationRequested(envelope.getPayload());
            default -> log.warn("Unknown event type {}", envelope.getEventType());
        }
    }

    private void handlePaymentRequested(String payload) throws Exception {
        PaymentRequest event = mapper.readValue(payload, PaymentRequest.class);
        kafka.send("payment-commands", mapper.writeValueAsString(event));
    }

    private void handleNotificationRequested(String payload) throws Exception {
        PaymentNotificationRequest event =
                mapper.readValue(payload, PaymentNotificationRequest.class);

        kafka.send("notification-commands", mapper.writeValueAsString(event));
    }

}

