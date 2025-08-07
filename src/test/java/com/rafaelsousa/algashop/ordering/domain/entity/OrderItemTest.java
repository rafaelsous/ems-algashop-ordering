package com.rafaelsousa.algashop.ordering.domain.entity;

import com.rafaelsousa.algashop.ordering.domain.valueobject.Product;
import com.rafaelsousa.algashop.ordering.domain.valueobject.Quantity;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.OrderId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderItemTest {

    @Test
    void shouldCreateBrandNewOrderItem() {
        OrderId orderId = new OrderId();
        Product product = ProductTestDataBuilder.aProduct().build();
        Quantity quantity = Quantity.of(1);

        OrderItem orderItem = OrderItem.brandNew()
                .orderId(orderId)
                .product(product)
                .quantity(quantity)
                .build();

        Assertions.assertWith(orderItem,
                oi -> Assertions.assertThat(oi.id()).isNotNull(),
                oi -> Assertions.assertThat(oi.orderId()).isEqualTo(orderId),
                oi -> Assertions.assertThat(oi.productId()).isEqualTo(product.id()),
                oi -> Assertions.assertThat(oi.productName()).isEqualTo(product.name()),
                oi -> Assertions.assertThat(oi.price()).isEqualTo(product.price()),
                oi -> Assertions.assertThat(oi.quantity()).isEqualTo(quantity)
        );
    }
}