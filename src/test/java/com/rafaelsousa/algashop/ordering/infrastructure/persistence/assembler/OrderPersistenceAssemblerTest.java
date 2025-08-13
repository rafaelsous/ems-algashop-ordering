package com.rafaelsousa.algashop.ordering.infrastructure.persistence.assembler;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Order;
import com.rafaelsousa.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderPersistenceAssemblerTest {
    private final OrderPersistenceAssembler assembler = new OrderPersistenceAssembler();

    @Test
    void shouldConvertFromDomakin() {
        Order order = OrderTestDataBuilder.anOrder().build();
        OrderPersistence orderPersistence = assembler.fromDomain(order);

        assertThat(orderPersistence).satisfies(
                op -> assertThat(op.getId()).isEqualTo(order.id().value().toLong()),
                op -> assertThat(op.getCustomerId()).isEqualTo(order.customerId().value()),
                op -> assertThat(op.getTotalAmount()).isEqualTo(order.totalAmount().value()),
                op -> assertThat(op.getTotalItems()).isEqualTo(order.totalItems().value()),
                op -> assertThat(op.getStatus()).isEqualTo(order.status().name()),
                op -> assertThat(op.getPaymentMethod()).isEqualTo(order.paymentMethod().name()),
                op -> assertThat(op.getPlacedAt()).isEqualTo(order.placedAt()),
                op -> assertThat(op.getPaidAt()).isEqualTo(order.paidAt()),
                op -> assertThat(op.getCanceledAt()).isEqualTo(order.canceledAt()),
                op -> assertThat(op.getReadyAt()).isEqualTo(order.readyAt())
        );
    }

    @Test
    void shouldMerge() {

    }
}