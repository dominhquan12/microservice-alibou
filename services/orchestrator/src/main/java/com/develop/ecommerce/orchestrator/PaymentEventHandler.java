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

        PaymentCompletedEvent event = mapper.readValue(message, PaymentCompletedEvent.class);

        log.info("Payment result for order {} : {}", event.getOrderId(), event.getStatus());

        if (event.getStatus().equals("SUCCESS")) {

            OrderApproveCommand cmd = new OrderApproveCommand(event.getOrderId());
            kafka.send("order-commands", mapper.writeValueAsString(cmd));

        } else {

            OrderCancelCommand cmd = new OrderCancelCommand(event.getOrderId());
            kafka.send("order-commands", mapper.writeValueAsString(cmd));
        }
    }

}
