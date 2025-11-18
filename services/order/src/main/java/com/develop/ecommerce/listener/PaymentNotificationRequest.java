package com.develop.ecommerce.listener;


import java.math.BigDecimal;

public record PaymentNotificationRequest(
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
