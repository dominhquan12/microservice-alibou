package com.develop.ecommerce.kafka.payment;

import java.math.BigDecimal;

public record PaymentConfirmation(
        Integer orderId,
        String orderReference,
        BigDecimal amount,
        PaymentMethod paymentMethod,
        String customerFirstname,
        String customerLastname,
        String customerEmail,
        PaymentStatus paymentStatus
) {
}
