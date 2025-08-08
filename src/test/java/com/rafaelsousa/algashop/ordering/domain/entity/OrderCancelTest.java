package com.rafaelsousa.algashop.ordering.domain.entity;

import com.rafaelsousa.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Arrays;

class OrderCancelTest {

    @Test
    void givenNotCanceledOrder_whenCancel_shouldAllow() {
        Arrays.stream(OrderStatus.values())
                .filter(status -> !OrderStatus.CANCELED.equals(status))
                .forEach(status -> {
                    Order order = OrderTestDataBuilder.anOrder()
                            .status(status)
                            .build();

                    order.cancel();
                    OffsetDateTime cancelededAt = order.canceledAt();

                    Assertions.assertWith(order,
                            o -> Assertions.assertThat(o.status()).isEqualTo(OrderStatus.CANCELED),
                            o -> Assertions.assertThat(o.canceledAt()).isNotNull(),
                            o -> Assertions.assertThat(o.canceledAt()).isEqualTo(cancelededAt)
                    );
                });
    }

    @Test
    void givenCanceledOrder_whenTryToCancel_shouldThrowException() {
        Order order = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.CANCELED)
                .build();

        Assertions.assertWith(order,
                o -> Assertions.assertThatExceptionOfType(OrderStatusCannotBeChangedException.class)
                        .isThrownBy(order::cancel),
                o -> Assertions.assertThat(o.status()).isEqualTo(OrderStatus.CANCELED),
                o -> Assertions.assertThat(o.canceledAt()).isNotNull()
        );
    }
}