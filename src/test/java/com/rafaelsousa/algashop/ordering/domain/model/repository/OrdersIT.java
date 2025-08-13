package com.rafaelsousa.algashop.ordering.domain.model.repository;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Order;
import com.rafaelsousa.algashop.ordering.domain.model.entity.OrderStatus;
import com.rafaelsousa.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.provider.OrdersPersistenceProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({OrdersPersistenceProvider.class, OrderPersistenceAssembler.class, OrderPersistenceDisassembler.class})
class OrdersIT {
    private final Orders orders;

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

        assertThat(orderOptional).isPresent();

        Order savedOrder = orderOptional.get();

        assertThat(savedOrder).satisfies(
                so -> assertThat(so.id()).isEqualTo(orderId),
                so -> assertThat(so.customerId()).isEqualTo(originalOrder.customerId()),
                so -> assertThat(so.totalAmount()).isEqualTo(originalOrder.totalAmount()),
                so -> assertThat(so.totalItems()).isEqualTo(originalOrder.totalItems()),
                so -> assertThat(so.status()).isEqualTo(originalOrder.status()),
                so -> assertThat(so.paymentMethod()).isEqualTo(originalOrder.paymentMethod()),
                so -> assertThat(so.placedAt()).isEqualTo(originalOrder.placedAt()),
                so -> assertThat(so.paidAt()).isEqualTo(originalOrder.paidAt()),
                so -> assertThat(so.canceledAt()).isEqualTo(originalOrder.canceledAt())
        );
    }

    @Test
    void shouldUpdateExistingOrder() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();

        orders.add(order);

        order = orders.ofId(order.id()).orElseThrow();

        order.markAsPaid();

        orders.add(order);

        order = orders.ofId(order.id()).orElseThrow();

        assertThat(order.isPaid()).isTrue();
    }
}