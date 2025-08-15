package com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Order;
import com.rafaelsousa.algashop.ordering.domain.model.entity.OrderStatus;
import com.rafaelsousa.algashop.ordering.domain.model.entity.PaymentMethod;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Money;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceTestDataBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OrderPersistenceDisassemblerTest {
    private final OrderPersistenceDisassembler disassembler = new OrderPersistenceDisassembler();

    @Test
    void shouldConvertFromPersistence() {
        OrderPersistence orderPersistence = OrderPersistenceTestDataBuilder.existingOrder().build();

        Order order = disassembler.toDomain(orderPersistence);

        assertThat(order).satisfies(
                o -> assertThat(o.id()).isEqualTo(new OrderId(orderPersistence.getId())),
                o -> assertThat(o.customerId()).isEqualTo(new CustomerId(orderPersistence.getCustomerId())),
                o -> assertThat(o.totalAmount()).isEqualTo(Money.of(orderPersistence.getTotalAmount())),
                o -> assertThat(o.totalItems()).isEqualTo(Quantity.of(orderPersistence.getTotalItems())),
                o -> assertThat(o.paymentMethod()).isEqualTo(PaymentMethod.valueOf(orderPersistence.getPaymentMethod())),
                o -> assertThat(o.placedAt()).isEqualTo(orderPersistence.getPlacedAt()),
                o -> assertThat(o.paidAt()).isEqualTo(orderPersistence.getPlacedAt()),
                o -> assertThat(o.readyAt()).isEqualTo(orderPersistence.getReadyAt()),
                o -> assertThat(o.canceledAt()).isEqualTo(orderPersistence.getCanceledAt()),
                o -> assertThat(o.status()).isEqualTo(OrderStatus.valueOf(orderPersistence.getStatus())),

                o -> assertThat(o.items()).hasSize(orderPersistence.getItems().size())
        );
    }
}