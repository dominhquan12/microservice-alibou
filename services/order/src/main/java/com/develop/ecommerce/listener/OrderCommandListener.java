package com.develop.ecommerce.listener;

import com.develop.ecommerce.order.OrderService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderCommandListener {

    private final OrderService orderService;
    private final ObjectMapper mapper;

    @KafkaListener(topics = "order-commands")
    public void onOrderCommand(String message) throws Exception {

        PaymentNotificationRequest paymentNotificationRequest = mapper.readValue(message, PaymentNotificationRequest.class);

        if (paymentNotificationRequest.paymentStatus() ==  PaymentStatus.SUCCESS) {
            orderService.approve(paymentNotificationRequest);
        } else if (paymentNotificationRequest.paymentStatus() ==  PaymentStatus.FAILED) {
            orderService.cancel(paymentNotificationRequest);
        }
    }
}
