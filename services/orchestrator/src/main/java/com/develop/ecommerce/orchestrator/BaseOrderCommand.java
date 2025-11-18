package com.develop.ecommerce.orchestrator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class BaseOrderCommand {
    private String orderId;
}


