package com.rafaelsousa.algashop.ordering.domain.entity;

import com.rafaelsousa.algashop.ordering.domain.valueobject.id.CustomerId;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void shouldCreateOrder() {
        var order = Order.draft(
                new CustomerId());

        Assertions.assertNotNull(order);
    }
}