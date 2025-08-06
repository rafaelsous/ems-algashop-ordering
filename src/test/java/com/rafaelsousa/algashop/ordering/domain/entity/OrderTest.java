package com.rafaelsousa.algashop.ordering.domain.entity;

import com.rafaelsousa.algashop.ordering.domain.exception.OrderInvalidShippingDeliveryDateException;
import com.rafaelsousa.algashop.ordering.domain.exception.OrderStatusCannotBeChangedException;
import com.rafaelsousa.algashop.ordering.domain.valueobject.*;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.ProductId;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Set;

class OrderTest {

    @Test
    void shouldCreateOrder() {
        var order = Order.draft(new CustomerId());

        Assertions.assertThat(order).isNotNull();
    }

    @Test
    void shouldAddItem() {
        String productName = "Product 1";
        ProductId productId = new ProductId();
        Money price = Money.of("10.0");
        Order order = Order.draft(new CustomerId());

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
        Order order = Order.draft(new CustomerId());

        order.addItem(new ProductId(), new ProductName("Product 1"), Money.of("10.00"), Quantity.of(1));

        Set<OrderItem> items = order.items();

        Assertions.assertThatThrownBy(items::clear)
                .isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    void shouldCalculateTotals() {
        Order order = Order.draft(new CustomerId());

        order.addItem(new ProductId(), new ProductName("Product 1"), Money.of("10.00"), Quantity.of(5));
        order.addItem(new ProductId(), new ProductName("Product 2"), Money.of("15.00"), Quantity.of(1));

        Assertions.assertThat(order.totalAmount()).isEqualTo(Money.of("65.00"));
        Assertions.assertThat(order.totalItems()).isEqualTo(Quantity.of(6));
    }

    @Test
    void givenDraftOrderWhenPlaceShouldChangeStatusToPlaced() {
        Order order = Order.draft(new CustomerId());

        order.place();

        Assertions.assertThat(order.isPlaced()).isTrue();
    }

    @Test
    void givenPlacedOrderWhenPlaceShouldThrowException() {
        Order order = Order.draft(new CustomerId());

        order.place();

        Assertions.assertThatThrownBy(order::place)
                .isInstanceOf(OrderStatusCannotBeChangedException.class);
    }

    @Test
    void givenDraftOrderWhenChangePaymentMethodShouldAllowChange() {
        Order order = Order.draft(new CustomerId());

        order.changePaymentMethod(PaymentMethod.CREDIT_CARD);

        Assertions.assertThat(order.paymentMethod()).isEqualTo(PaymentMethod.CREDIT_CARD);
    }

    @Test
    void givenDraftOrderWhenChangeBillingInfoShouldAllowChange() {
        Order order = Order.draft(new CustomerId());
        Address address = Address.builder()
                .street("Bourbon Street")
                .neighborhood("North Ville")
                .number("123")
                .city("New York")
                .state("South California")
                .zipCode(new ZipCode("12345"))
                .build();
        BillingInfo billing = BillingInfo.builder()
                .address(address)
                .document(Document.of("123-12-1234"))
                .phone(Phone.of("123-123-1234"))
                .fullName(FullName.of("Rafael", "Sousa"))
                .build();

        order.changeBilling(billing);

        Assertions.assertThat(order.billing()).isEqualTo(billing);
    }

    @Test
    void givenDraftOrderWhenChangeShippingInfoShouldAllowChange() {
        Order order = Order.draft(new CustomerId());
        Address address = Address.builder()
                .street("Bourbon Street")
                .neighborhood("North Ville")
                .number("123")
                .city("New York")
                .state("South California")
                .zipCode(new ZipCode("12345"))
                .build();
        ShippingInfo shipping = ShippingInfo.builder()
                .address(address)
                .document(Document.of("123-12-1234"))
                .phone(Phone.of("123-123-1234"))
                .fullName(FullName.of("Rafael", "Sousa"))
                .build();

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
        Order order = Order.draft(new CustomerId());
        Address address = Address.builder()
                .street("Bourbon Street")
                .neighborhood("North Ville")
                .number("123")
                .city("New York")
                .state("South California")
                .zipCode(new ZipCode("12345"))
                .build();
        ShippingInfo shipping = ShippingInfo.builder()
                .address(address)
                .document(Document.of("123-12-1234"))
                .phone(Phone.of("123-123-1234"))
                .fullName(FullName.of("Rafael", "Sousa"))
                .build();

        Money shippingCost = Money.of("13.99");
        LocalDate expectedDeliveryDate = LocalDate.now().minusDays(5);

        Assertions.assertThatExceptionOfType(OrderInvalidShippingDeliveryDateException.class)
                .isThrownBy(() -> order.changeShipping(shipping, shippingCost, expectedDeliveryDate));
    }
}