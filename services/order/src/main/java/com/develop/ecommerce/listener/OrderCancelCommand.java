package com.develop.ecommerce.listener;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderCancelCommand extends BaseOrderCommand {
    public OrderCancelCommand(String orderId) {
        super(orderId);
    }
}
