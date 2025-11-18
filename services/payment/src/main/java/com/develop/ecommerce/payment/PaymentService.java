package com.develop.ecommerce.payment;

import com.develop.ecommerce.listener.PaymentStatus;
import com.develop.ecommerce.notification.NotificationProducer;
import com.develop.ecommerce.notification.PaymentNotificationRequest;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final NotificationProducer notificationProducer;

    public Integer createPayment(PaymentRequest request) {
        var payment = this.repository.save(this.mapper.toPayment(request));

        this.notificationProducer.sendNotification(
                new PaymentNotificationRequest(
                        payment.getOrderId(),
                        request.orderReference(),
                        request.amount(),
                        request.paymentMethod(),
                        request.customer().firstname(),
                        request.customer().lastname(),
                        request.customer().email(),
                        PaymentStatus.PENDING
                )
        );
        return payment.getId();
    }

    @SneakyThrows
    public Payment process(PaymentRequest request) {
        var payment = this.repository.save(this.mapper.toPayment(request));
        payment.setStatus(PaymentStatus.SUCCESS);
        repository.save(payment);
        return payment;
    }
}

