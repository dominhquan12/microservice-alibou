package com.develop.ecommerce.listener;

import com.develop.ecommerce.outbox.OutboxStatus;
import com.develop.ecommerce.outbox.PaymentOutbox;
import com.develop.ecommerce.outbox.PaymentOutboxRepository;
import com.develop.ecommerce.payment.Payment;
import com.develop.ecommerce.payment.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentCommandListener {

    private final PaymentService paymentService;
    private final PaymentOutboxRepository repo;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "payment-commands")
    public void onPaymentCommand(String message) throws Exception {

        PaymentProcessCommand cmd = mapper.readValue(message, PaymentProcessCommand.class);

        Payment payment = paymentService.process(cmd);

        PaymentCompletedEvent event = new PaymentCompletedEvent(
                payment.getOrderId().toString(),
                payment.getStatus().name()
        );

        PaymentOutbox outbox = PaymentOutbox.builder()
                .eventType("PaymentCompleted")
                .payload(mapper.writeValueAsString(event))
                .status(OutboxStatus.PENDING)
                .createdAt(Instant.now())
                .build();

        repo.save(outbox);
    }
}
