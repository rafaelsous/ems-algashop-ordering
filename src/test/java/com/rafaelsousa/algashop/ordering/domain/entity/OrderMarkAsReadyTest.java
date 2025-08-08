package com.rafaelsousa.algashop.ordering.domain.entity;

import com.rafaelsousa.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Arrays;

class OrderMarkAsReadyTest {

    @Test
    void givenPaidOrder_whenChangeStatusToReady_shouldAllow() {
        Order order = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.PAID)
                .build();

        order.markAsReady();
        OffsetDateTime readiedAt = order.readyAt();

        Assertions.assertWith(order,
                o -> Assertions.assertThat(o.status()).isEqualTo(OrderStatus.READY),
                o -> Assertions.assertThat(o.readyAt()).isNotNull(),
                o -> Assertions.assertThat(o.isReady()).isTrue(),
                o -> Assertions.assertThat(o.readyAt()).isEqualTo(readiedAt)
        );
    }

    @Test
    void givenUnpaidOrder_whenTryToChangeStatusToReady_shouldNotAllow() {
        Arrays.stream(OrderStatus.values())
                .filter(status -> !OrderStatus.PAID.equals(status)
                        && !OrderStatus.READY.equals(status)
                        && !OrderStatus.CANCELED.equals(status)
                )
                .forEach(status -> {
                            Order order = OrderTestDataBuilder.anOrder()
                                    .status(status)
                                    .build();

                            System.out.println(status);

                            Assertions.assertThatThrownBy(order::markAsReady)
                                    .isInstanceOf(OrderStatusCannotBeChangedException.class);

                            Assertions.assertThat(order.status()).isEqualTo(status);
                            Assertions.assertThat(order.readyAt()).isNull();
                        }
                );
    }
}