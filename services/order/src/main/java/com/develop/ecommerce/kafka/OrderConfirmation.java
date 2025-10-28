package com.develop.ecommerce.kafka;

import com.develop.ecommerce.customer.CustomerResponse;
import com.develop.ecommerce.order.PaymentMethod;
import com.develop.ecommerce.product.PurchaseResponse;

import java.math.BigDecimal;
import java.util.List;

public record OrderConfirmation (
        String orderReference,
        BigDecimal totalAmount,
        PaymentMethod paymentMethod,
        CustomerResponse customer,
        List<PurchaseResponse> products

) {
}
