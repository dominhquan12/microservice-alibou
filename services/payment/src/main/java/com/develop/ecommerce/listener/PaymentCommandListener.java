package com.develop.ecommerce.listener;

import com.develop.ecommerce.notification.PaymentNotificationRequest;
import com.develop.ecommerce.outbox.OutboxStatus;
import com.develop.ecommerce.outbox.PaymentOutbox;
import com.develop.ecommerce.outbox.PaymentOutboxRepository;
import com.develop.ecommerce.payment.Payment;
import com.develop.ecommerce.payment.PaymentRequest;
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
    private final PaymentOutboxRepository paymentOutboxRepository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "payment-commands")
    public void onPaymentCommand(String message) throws Exception {

        PaymentRequest paymentRequest = objectMapper.readValue(message, PaymentRequest.class);

        Payment payment = paymentService.process(paymentRequest);

        // Gửi notification
        PaymentOutbox outbox = new PaymentOutbox();
        outbox.setEventType("PAYMENT_NOTIFICATION");
        outbox.setPayload(objectMapper.writeValueAsString(
                new PaymentNotificationRequest(
                        payment.getOrderId(),
                        paymentRequest.orderReference(),
                        paymentRequest.amount(),
                        paymentRequest.paymentMethod(),
                        paymentRequest.customer().firstname(),
                        paymentRequest.customer().lastname(),
                        paymentRequest.customer().email(),
                        payment.getStatus()
                )
        ));
        outbox.setStatus(OutboxStatus.PENDING);
        outbox.setCreatedAt(Instant.now());
        paymentOutboxRepository.save(outbox);
    }
}
