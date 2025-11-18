package com.develop.ecommerce.kafka.payment;

public enum PaymentStatus {
    PENDING,    // Đang chờ thanh toán
    SUCCESS,    // Thanh toán thành công
    FAILED      // Thanh toán thất bại
}
