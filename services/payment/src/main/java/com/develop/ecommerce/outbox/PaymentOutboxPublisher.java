package com.develop.ecommerce.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentOutboxPublisher {

    private final PaymentOutboxRepository repo;
    private final KafkaTemplate<String, String> kafka;

    @Scheduled(fixedDelay = 1000)
    public void publishPaymentEvents() {
        var events = repo.findByStatus(OutboxStatus.PENDING);

        for (var e : events) {
            try {
                kafka.send("payment-events", e.getPayload()).get();
                e.setStatus(OutboxStatus.SENT);
                repo.save(e);
            } catch (Exception ex) {
                log.error("Payment publish failed", ex);
                e.setStatus(OutboxStatus.FAILED);
                repo.save(e);
            }
        }
    }
}

