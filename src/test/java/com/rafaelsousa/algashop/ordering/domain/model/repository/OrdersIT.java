package com.rafaelsousa.algashop.ordering.domain.model.repository;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Order;
import com.rafaelsousa.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.provider.OrdersPersistenceProvider;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

@DataJpaTest
@Import(OrdersPersistenceProvider.class)
class OrdersIT {
    private Orders orders;

    @Autowired
    public OrdersIT(Orders orders) {
        this.orders = orders;
    }

    @Test
    void shouldPersistAndFind() {
        Order originalOrder = OrderTestDataBuilder.anOrder().build();
        OrderId orderId = originalOrder.id();

        orders.add(originalOrder);
        Optional<Order> orderOptional = orders.ofId(orderId);

        Assertions.assertThat(orderOptional).isPresent();

        Order savedOrder = orderOptional.get();

        Assertions.assertThat(savedOrder).satisfies(
                so -> Assertions.assertThat(so.id()).isEqualTo(orderId),
                so -> Assertions.assertThat(so.customerId()).isEqualTo(originalOrder.customerId()),
                so -> Assertions.assertThat(so.totalAmount()).isEqualTo(originalOrder.totalAmount()),
                so -> Assertions.assertThat(so.totalItems()).isEqualTo(originalOrder.totalItems()),
                so -> Assertions.assertThat(so.status()).isEqualTo(originalOrder.status()),
                so -> Assertions.assertThat(so.paymentMethod()).isEqualTo(originalOrder.paymentMethod()),
                so -> Assertions.assertThat(so.placedAt()).isEqualTo(originalOrder.placedAt()),
                so -> Assertions.assertThat(so.paidAt()).isEqualTo(originalOrder.paidAt()),
                so -> Assertions.assertThat(so.canceledAt()).isEqualTo(originalOrder.canceledAt())
        );
    }
}