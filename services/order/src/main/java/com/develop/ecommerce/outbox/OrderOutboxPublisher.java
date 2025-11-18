package com.develop.ecommerce.outbox;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderOutboxPublisher {

    private final OrderOutboxRepository repository;
    private final KafkaTemplate<String, String> kafka;

    @Scheduled(fixedDelay = 1000)
    public void publishPending() {
        var events = repository.findByStatus(OutboxStatus.PENDING);

        for (var e : events) {
            try {
                kafka.send("order-events", e.getPayload()).get();
                e.setStatus(OutboxStatus.SENT);
                repository.save(e);
            } catch (Exception ex) {
                log.error("Publish failed", ex);
                e.setStatus(OutboxStatus.FAILED);
                repository.save(e);
            }
        }
    }
}
