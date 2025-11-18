package com.develop.ecommerce.orchestrator;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public abstract class BaseOrderCommand {

    private String orderId;

    public BaseOrderCommand(String orderId) {
        this.orderId = orderId;
    }
}


