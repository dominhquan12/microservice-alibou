package com.develop.ecommerce.orchestrator;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentEventHandler {

    private final KafkaTemplate<String, String> kafka;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "payment-events")
    public void handlePaymentEvents(String message) throws Exception {

        // Bước 1: unescape JSON string
        String cleaned = mapper.readValue(message, String.class);

        // Bước 2: parse JSON thực sự
        PaymentNotificationRequest event =
                mapper.readValue(cleaned, PaymentNotificationRequest.class);

        log.info("Payment result for order {}", event);

        kafka.send("order-commands", mapper.writeValueAsString(event));
    }


}
