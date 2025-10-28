package com.alibou.ecommerce.kafka.order;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Optional;

@Data
public class Product {
    Integer productId;
    String name;
    String description;
    BigDecimal price;
    Integer quantity;
    BigDecimal totalPrice;

    public void setPrice(BigDecimal price) {
        this.price = price;
        recalcTotal();
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
        recalcTotal();
    }

    private void recalcTotal() {
        this.totalPrice = (price != null && quantity != null)
                ? price.multiply(BigDecimal.valueOf(quantity))
                : BigDecimal.ZERO;
    }
}
