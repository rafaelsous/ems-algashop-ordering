package com.rafaelsousa.algashop.ordering.domain.entity;

import com.rafaelsousa.algashop.ordering.domain.valueobject.Quantity;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.OrderId;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void shouldCreateOrderItem() {
        OrderItem.brandNew()
                .orderId(new OrderId())
                .product(ProductTestDataBuilder.aProduct().build())
                .quantity(Quantity.of(1))
                .build();
    }
}