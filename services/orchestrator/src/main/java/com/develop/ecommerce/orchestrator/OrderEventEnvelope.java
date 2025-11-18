package com.develop.ecommerce.orchestrator;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderEventEnvelope {
    private String eventType;   // PAYMENT_REQUESTED / NOTIFICATION_REQUESTED
    private String payload;     // JSON của PaymentRequest hoặc PaymentNotificationRequest
}