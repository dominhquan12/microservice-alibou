package com.develop.ecommerce.kafka.payment;

import lombok.Data;

@Data
public class Address {
    private String street;
    private String houseNumber;
    private String zipCode;
}
