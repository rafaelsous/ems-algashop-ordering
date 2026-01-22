package com.rafaelsousa.algashop.ordering.core.application.shoppingcart;

import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.ShoppingCartItemOutput;
import com.rafaelsousa.algashop.ordering.core.ports.in.shopping.ShoppingCartOutput;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;


public class ShoppingCartOutputTestDataBuilder {

    public static ShoppingCartOutput.ShoppingCartOutputBuilder aShoppingCart() {
        return ShoppingCartOutput.builder()
                .id(UUID.randomUUID())
                .customerId(UUID.randomUUID())
                .totalItems(2)
                .totalAmount(BigDecimal.valueOf(250.00))
                .items(List.of(
                        existingItem().build(),
                        existingAltItem().build()
                ));
    }

    public static ShoppingCartItemOutput.ShoppingCartItemOutputBuilder existingItem() {
        return ShoppingCartItemOutput.builder()
                .id(UUID.randomUUID())
                .productId(UUID.randomUUID())
                .name("Mouse pad")
                .price(BigDecimal.valueOf(100.00))
                .quantity(1)
                .totalAmount(BigDecimal.valueOf(100.00))
                .available(true);
    }

    public static ShoppingCartItemOutput.ShoppingCartItemOutputBuilder existingAltItem() {
        return ShoppingCartItemOutput.builder()
                .productId(UUID.randomUUID())
                .name("4G RAM")
                .price(BigDecimal.valueOf(150.00))
                .quantity(1)
                .totalAmount(BigDecimal.valueOf(150.00))
                .available(true);
    }
}