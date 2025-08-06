package com.rafaelsousa.algashop.ordering.domain.entity;

import com.rafaelsousa.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.rafaelsousa.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.rafaelsousa.algashop.ordering.domain.valueobject.*;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.ProductId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

class OrderTest {

    @Test
    void shouldCreateOrder() {
        var order = OrderTestDataBuilder.anOrder();

        Assertions.assertThat(order).isNotNull();
    }

    @Test
    void shouldAddItem() {
        String productName = "Product 1";
        ProductId productId = new ProductId();
        Money price = Money.of("10.0");
        Order order = OrderTestDataBuilder.anOrder()
                .withItems(false)
                .build();

        order.addItem(productId, new ProductName(productName), price, Quantity.of(1));

        OrderItem orderItem = order.items().iterator().next();

        Assertions.assertThat(order.items()).isNotEmpty();
        Assertions.assertThat(order.items()).hasSize(1);
        Assertions.assertWith(orderItem,
                oi -> Assertions.assertThat(oi.id()).isNotNull(),
                oi -> Assertions.assertThat(oi.productName().value()).hasToString(productName),
                oi -> Assertions.assertThat(oi.price()).isEqualTo(price),
                oi -> Assertions.assertThat(oi.productId()).isEqualTo(productId)
        );
    }

    @Test
    void shouldGenerateExceptionWhenTryToChangeItemsSet() {
        Order order = OrderTestDataBuilder.anOrder().build();

        Set<OrderItem> items = order.items();

        Assertions.assertThatThrownBy(items::clear)
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldCalculateTotals() {
        Order order = OrderTestDataBuilder.anOrder().build();

        Assertions.assertThat(order.totalItems()).isEqualTo(Quantity.of(6));
        Assertions.assertThat(order.totalAmount()).isEqualTo(Money.of("65.00"));
    }

    @Test
    void givenDraftOrderWhenPlaceShouldChangeStatusToPlaced() {
        Order order = OrderTestDataBuilder.anOrder().build();

        order.place();

        Assertions.assertThat(order.isPlaced()).isTrue();
    }

    @Test
    void givenPlacedOrderWhenPayShouldChangeStatusToPaid() {
        Order order = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.PLACED)
                .build();

        order.markAsPaid();

        Assertions.assertThat(order.isPaid()).isTrue();
        Assertions.assertThat(order.paidAt()).isNotNull();
    }

    @Test
    void givenPlacedOrderWhenPlaceShouldThrowException() {
        Order order = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.PLACED)
                .build();

        Assertions.assertThatThrownBy(order::place)
                .isInstanceOf(OrderStatusCannotBeChangedException.class);
    }

    @Test
    void givenDraftOrderWhenChangePaymentMethodShouldAllowChange() {
        Order order = OrderTestDataBuilder.anOrder()
                .paymentMethod(PaymentMethod.GATEWAY_BALANCE)
                .build();

        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);

        Assertions.assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
    }

    @Test
    void givenDraftOrderWhenChangeBillingInfoShouldAllowChange() {
        Order order = OrderTestDataBuilder.anOrder().build();
        BillingInfo billing = OrderTestDataBuilder.generateBillingInfo();

        order.changeBilling(billing);

        Assertions.assertThat(order.billing()).isEqualTo(billing);
    }

    @Test
    void givenDraftOrderWhenChangeShippingInfoShouldAllowChange() {
        Order order = OrderTestDataBuilder.anOrder().build();
        ShippingInfo shipping = OrderTestDataBuilder.generateShippingInfo();

        Money shippingCost = Money.of("13.99");
        LocalDate expectedDeliveryDate = LocalDate.now().plusDays(5);
        order.changeShipping(shipping, shippingCost, expectedDeliveryDate);

        Assertions.assertWith(order,
                o -> Assertions.assertThat(order.shipping()).isEqualTo(shipping),
                o -> Assertions.assertThat(order.shippingCost()).isEqualTo(shippingCost),
                o -> Assertions.assertThat(order.expectedDeliveryDate()).isEqualTo(expectedDeliveryDate)
        );
    }

    @Test
    void givenDraftOrderAndDeliveryDateInThePastWhenChangeShippingInfoShouldNotAllowChange() {
        Order order = OrderTestDataBuilder.anOrder().build();
        ShippingInfo shipping = OrderTestDataBuilder.generateShippingInfo();

        Money shippingCost = Money.of("13.99");
        LocalDate expectedDeliveryDate = LocalDate.now().minusDays(5);

        Assertions.assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
                .isThrownBy(() -> order.changeShipping(shipping, shippingCost, expectedDeliveryDate));
    }
}