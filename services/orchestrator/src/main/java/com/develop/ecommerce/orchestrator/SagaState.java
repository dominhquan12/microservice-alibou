package com.develop.ecommerce.orchestrator;

public enum SagaState {
    ORDER_CREATED,
    PAYMENT_PROCESSING,
    PAYMENT_SUCCESS,
    PAYMENT_FAILED
}
