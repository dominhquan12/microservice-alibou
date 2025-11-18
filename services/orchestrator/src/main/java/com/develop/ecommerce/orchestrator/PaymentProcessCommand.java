package com.develop.ecommerce.orchestrator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentProcessCommand {
    private String orderId;
    private BigDecimal amount;
}

