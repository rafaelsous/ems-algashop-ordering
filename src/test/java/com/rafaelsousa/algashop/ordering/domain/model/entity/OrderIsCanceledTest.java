package com.rafaelsousa.algashop.ordering.domain.model.entity;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

class OrderIsCanceledTest {

    @Test
    void givenNotCanceledOrder_whenCallIsCanceled_shouldReturnFalse() {
        Arrays.stream(OrderStatus.values())
                .filter(status -> !OrderStatus.CANCELED.equals(status))
                .forEach(status -> {
                    Order order = OrderTestDataBuilder.anOrder()
                            .status(status)
                            .build();

                    Assertions.assertThat(order.isCanceled()).isFalse();
                });
    }

    @Test
    void givenCanceledOrder_whenCallIsCanceled_shouldReturnTrue() {
        Order order = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.CANCELED)
                .build();

        Assertions.assertThat(order.isCanceled()).isTrue();
    }
}