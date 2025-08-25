package com.rafaelsousa.algashop.ordering.application.order.management;

import com.rafaelsousa.algashop.ordering.application.customer.management.CustomerLoyaltyPointsApplicationService;
import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customers;
import com.rafaelsousa.algashop.ordering.domain.model.order.*;
import com.rafaelsousa.algashop.ordering.infrastructure.listener.order.OrderEventListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@Transactional
@SpringBootTest
class OrderManagementApplicationServiceIT {
    private final Orders orders;
    private final Customers customers;
    private final OrderManagementApplicationService orderManagementApplicationService;

    @MockitoSpyBean
    private OrderEventListener orderEventListener;

    @MockitoSpyBean
    private CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService;

    @Autowired
    OrderManagementApplicationServiceIT(Orders orders, Customers customers, OrderManagementApplicationService orderManagementApplicationService) {
        this.orders = orders;
        this.customers = customers;
        this.orderManagementApplicationService = orderManagementApplicationService;
    }

    @BeforeEach
    void setUp() {
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    void shouldCancelOrderSuccessfully() {
        Order order = OrderTestDataBuilder.anOrder().customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID).build();
        orders.add(order);

        long rawOrderId = order.id().value().toLong();
        orderManagementApplicationService.cancel(rawOrderId);

        Order updatedOrder = orders.ofId(order.id()).orElseThrow();

        assertThat(updatedOrder).satisfies(
                o -> assertThat(o.isCanceled()).isTrue(),
                o -> assertThat(o.canceledAt()).isNotNull()
        );

        verify(orderEventListener).listen(any(OrderCanceledEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenTryingToCancelNonExistentOrder() {
        OrderId nonExistentOrderId = new OrderId();
        long rawOrderId = nonExistentOrderId.value().toLong();

        assertThatThrownBy(() -> orderManagementApplicationService.cancel(rawOrderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage(ErrorMessages.ERROR_ORDER_NOT_FOUND.formatted(nonExistentOrderId));
    }

    @Test
    void shouldThrowExceptionWhenTryingCancelOrderAlreadyCanceled() {
        OrderStatus canceledStatus = OrderStatus.CANCELED;
        Order order = OrderTestDataBuilder.anOrder().customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .status(canceledStatus).build();
        orders.add(order);

        long rawOrderId = order.id().value().toLong();

        assertThatThrownBy(() -> orderManagementApplicationService.cancel(rawOrderId))
                .isInstanceOf(OrderStatusCannotBeChangedException.class)
                .hasMessage(ErrorMessages.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED.formatted(order.id(),
                        order.status(), canceledStatus));
    }

    @Test
    void shouldMarkOrderAsPaidSuccessfully() {
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .status(OrderStatus.PLACED)
                .build();
        orders.add(order);

        long rawOrderId = order.id().value().toLong();
        orderManagementApplicationService.markAsPaid(rawOrderId);

        Order updatedOrder = orders.ofId(order.id()).orElseThrow();

        assertThat(updatedOrder).satisfies(
                o -> assertThat(o.isPaid()).isTrue(),
                o -> assertThat(o.paidAt()).isNotNull()
        );

        verify(orderEventListener).listen(any(OrderPaidEvent.class));
    }

    @Test
    void shouldThrowExceptionWhenTryingToMarkNonExistentOrderAsPaid() {
        OrderId nonExistentOrderId = new OrderId();
        long rawOrderId = nonExistentOrderId.value().toLong();

        assertThatThrownBy(() -> orderManagementApplicationService.markAsPaid(rawOrderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage(ErrorMessages.ERROR_ORDER_NOT_FOUND.formatted(nonExistentOrderId));
    }

    @Test
    void shouldThrowExceptionWhenTryingMarkCanceledOrderAsPaid() {
        Order order = OrderTestDataBuilder.anOrder().customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .status(OrderStatus.CANCELED).build();
        orders.add(order);

        long rawOrderId = order.id().value().toLong();

        assertThatThrownBy(() -> orderManagementApplicationService.markAsPaid(rawOrderId))
                .isInstanceOf(OrderStatusCannotBeChangedException.class)
                .hasMessage(ErrorMessages.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED.formatted(order.id(),
                        order.status(), OrderStatus.PAID));

        order = orders.ofId(order.id()).orElseThrow();

        assertThat(order).satisfies(
                o -> assertThat(o.isPaid()).isFalse(),
                o -> assertThat(o.paidAt()).isNull()
        );
    }

    @Test
    void shouldMarkOrderAsReadySuccessfully() {
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .status(OrderStatus.PAID).build();
        orders.add(order);

        long rawOrderId = order.id().value().toLong();
        orderManagementApplicationService.markAsReady(rawOrderId);

        Order updatedOrder = orders.ofId(order.id()).orElseThrow();

        assertThat(updatedOrder).satisfies(
                o -> assertThat(o.isReady()).isTrue(),
                o -> assertThat(o.paidAt()).isNotNull()
        );

        verify(orderEventListener).listen(any(OrderReadyEvent.class));
        verify(customerLoyaltyPointsApplicationService).addLoyaltyPoints(any(UUID.class), any(String.class));

    }

    @Test
    void shouldThrowExceptionWhenTryingToMarkNonExistentOrderAsReady() {
        OrderId nonExistentOrderId = new OrderId();
        long rawOrderId = nonExistentOrderId.value().toLong();

        assertThatThrownBy(() -> orderManagementApplicationService.markAsReady(rawOrderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage(ErrorMessages.ERROR_ORDER_NOT_FOUND.formatted(nonExistentOrderId));
    }

    @Test
    void shouldThrowExceptionWhenTryingMarkCanceledOrderAsReady() {
        Order order = OrderTestDataBuilder.anOrder()
                .customerId(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)
                .status(OrderStatus.CANCELED).build();
        orders.add(order);

        long rawOrderId = order.id().value().toLong();

        assertThatThrownBy(() -> orderManagementApplicationService.markAsReady(rawOrderId))
                .isInstanceOf(OrderStatusCannotBeChangedException.class)
                .hasMessage(ErrorMessages.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED.formatted(order.id(),
                        order.status(), OrderStatus.READY));

        order = orders.ofId(order.id()).orElseThrow();

        assertThat(order).satisfies(
                o -> assertThat(o.isReady()).isFalse(),
                o -> assertThat(o.paidAt()).isNull()
        );
    }
}