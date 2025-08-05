package com.rafaelsousa.algashop.ordering.domain.entity;

import com.rafaelsousa.algashop.ordering.domain.valueobject.Money;
import com.rafaelsousa.algashop.ordering.domain.valueobject.ProductName;
import com.rafaelsousa.algashop.ordering.domain.valueobject.Quantity;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.ProductId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderTest {

    @Test
    void shouldCreateOrder() {
        var order = Order.draft(new CustomerId());

        Assertions.assertThat(order).isNotNull();
    }

    @Test
    void shouldAddItem() {
        String productName = "Product 1";
        ProductId productId = new ProductId();
        Money price = Money.of("10.0");
        Order order = Order.draft(new CustomerId());

        order.addItem(productId, new ProductName(productName), price, Quantity.of(1));

        OrderItem orderItem = order.items().iterator().next();

        Assertions.assertThat(order.items()).isNotEmpty();
        Assertions.assertThat(order.items()).hasSize(1);
        Assertions.assertWith(orderItem,
                oi -> Assertions.assertThat(oi.id()).isNotNull(),
                oi -> Assertions.assertThat(oi.productName().value()).hasToString(productName),
                oi -> Assertions.assertThat(oi.price()).isEqualTo(price),
                oi -> Assertions.assertThat(oi.productId()).isEqualTo(productId)
        );
    }
}