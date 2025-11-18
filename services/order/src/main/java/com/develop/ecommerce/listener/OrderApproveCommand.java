package com.develop.ecommerce.listener;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class OrderApproveCommand extends BaseOrderCommand {
    public OrderApproveCommand(String orderId) {
        super(orderId);
    }
}



