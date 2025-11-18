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

        PaymentNotificationRequest event = mapper.readValue(message, PaymentNotificationRequest.class);

        log.info("Payment result for order {}", event);

        if (event.paymentStatus() == PaymentStatus.SUCCESS) {
            kafka.send("order-commands", mapper.writeValueAsString(event));
        } else {
            kafka.send("order-commands", mapper.writeValueAsString(event));
        }
    }

}
