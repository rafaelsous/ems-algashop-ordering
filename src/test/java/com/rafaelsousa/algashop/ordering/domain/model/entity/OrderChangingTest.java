package com.rafaelsousa.algashop.ordering.domain.model.entity;

import com.rafaelsousa.algashop.ordering.domain.model.exception.OrderCannotBeEditedException;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Billing;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Product;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Shipping;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.OrderItemId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

class OrderChangingTest {
    private Product product;
    private Quantity quantity;
    private Billing billing;
    private Shipping shipping;
    private PaymentMethod paymentMethod;

    private Order order;
    private OrderItemId orderItemId;

    @BeforeEach
    void setup() {
        product = ProductTestDataBuilder.aProductAltMousePad().build();
        quantity = Quantity.of(2);
        billing = OrderTestDataBuilder.aBilling();
        shipping = OrderTestDataBuilder.aShipping();
        paymentMethod = PaymentMethod.CREDIT_CARD;

        order = OrderTestDataBuilder.anOrder().build();
        orderItemId = order.items().iterator().next().id();
    }

    @Test
    void givenDraftOrder_whenChange_shouldAllow() {
        List<Assertion> operations = getOperations();

        operations.forEach(operation -> Assertions.assertThatCode(operation::check).doesNotThrowAnyException());
    }

    @Test
    void givenPlacedOrder_whenTryToChange_shouldNotAllow() {
        order = withStatus(OrderStatus.PLACED);

        List<Assertion> operations = getOperations();

        operations.forEach(operation -> Assertions.assertThatThrownBy(operation::check).isInstanceOf(OrderCannotBeEditedException.class));
    }

    @Test
    void givenPaidOrder_whenTryToChange_shouldNotAllow() {
        order = withStatus(OrderStatus.PAID);

        List<Assertion> operations = getOperations();

        operations.forEach(operation -> Assertions.assertThatThrownBy(operation::check).isInstanceOf(OrderCannotBeEditedException.class));
    }

    @Test
    void givenReadyOrder_whenTryToChange_shouldNotAllow() {
        order = withStatus(OrderStatus.READY);

        List<Assertion> operations = getOperations();

        operations.forEach(operation -> Assertions.assertThatThrownBy(operation::check).isInstanceOf(OrderCannotBeEditedException.class));
    }

    @Test
    void givenCanceledOrder_whenTryToChange_shouldNotAllow() {
        order = withStatus(OrderStatus.CANCELED);

        List<Assertion> operations = getOperations();

        operations.forEach(operation -> Assertions.assertThatThrownBy(operation::check).isInstanceOf(OrderCannotBeEditedException.class));
    }

    private Order withStatus(OrderStatus status) {
        return OrderTestDataBuilder.anOrder()
                .status(status)
                .build();
    }

    private List<Assertion> getOperations() {
        return List.of(
                () -> order.addItem(product, quantity),
                () -> order.changeBilling(billing),
                () -> order.changeShipping(shipping),
                () -> order.changeItemQuantity(orderItemId, quantity),
                () -> order.changePaymentMethod(paymentMethod)
        );
    }

    @FunctionalInterface
    private interface Assertion {
        void check();
    }
}