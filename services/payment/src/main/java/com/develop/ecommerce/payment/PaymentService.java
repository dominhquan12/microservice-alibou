package com.develop.ecommerce.payment;

import com.develop.ecommerce.listener.PaymentProcessCommand;
import com.develop.ecommerce.listener.PaymentStatus;
import com.develop.ecommerce.notification.NotificationProducer;
import com.develop.ecommerce.notification.PaymentNotificationRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final NotificationProducer notificationProducer;

    // Method hiện tại
    public Integer createPayment(PaymentRequest request) {
        var payment = this.repository.save(this.mapper.toPayment(request));

        this.notificationProducer.sendNotification(
                new PaymentNotificationRequest(
                        request.orderReference(),
                        request.amount(),
                        request.paymentMethod(),
                        request.customer().firstname(),
                        request.customer().lastname(),
                        request.customer().email()
                )
        );
        return payment.getId();
    }

    // NEW: Process Payment (dùng cho Saga)
    public Payment process(PaymentProcessCommand cmd) {

        // Tạo Payment mới
        Payment payment = Payment.builder()
                .orderId(Integer.parseInt(cmd.getOrderId()))
                .amount(cmd.getAmount())
                .status(PaymentStatus.PENDING)
                .paymentMethod(cmd.getPaymentMethod()) // nếu cmd có
                .build();

        repository.save(payment);

        // Giả lập thanh toán thành công
        payment.setStatus(PaymentStatus.SUCCESS);
        repository.save(payment);

        // Gửi notification
        this.notificationProducer.sendNotification(
                new PaymentNotificationRequest(
                        cmd.getOrderId(),
                        cmd.getAmount(),
                        cmd.getPaymentMethod(),
                        cmd.getCustomerFirstName(),
                        cmd.getCustomerLastName(),
                        cmd.getCustomerEmail()
                )
        );

        return payment;
    }
}

