package com.rafaelsousa.algashop.ordering.core.domain.model.order;

import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderStatus;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

class OrderStatusTest {

    @Test
    void canChangeTo() {
        Assertions.assertThat(OrderStatus.DRAFT.canChangeTo(OrderStatus.PLACED)).isTrue();
        Assertions.assertThat(OrderStatus.DRAFT.canChangeTo(OrderStatus.CANCELED)).isTrue();
        Assertions.assertThat(OrderStatus.PAID.canChangeTo(OrderStatus.DRAFT)).isFalse();
    }

    @Test
    void canNotChangeTo() {
        Assertions.assertThat(OrderStatus.DRAFT.canNotChangeTo(OrderStatus.PAID)).isTrue();
        Assertions.assertThat(OrderStatus.PLACED.canNotChangeTo(OrderStatus.DRAFT)).isTrue();
    }
}