package com.rafaelsousa.algashop.ordering.domain.model.service;

import com.rafaelsousa.algashop.ordering.domain.model.entity.*;
import com.rafaelsousa.algashop.ordering.domain.model.exception.ShoppingCartCantProceedToCheckoutException;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.*;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class CheckoutServiceTest {
    private final CheckoutService checkoutService = new CheckoutService();

    @Test
    void givenEmptyShoppingCart_whenCallCheckout_shouldThrowException() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();

        assertThatThrownBy(() -> checkoutService.checkout(shoppingCart, null, null, null))
                .isInstanceOf(ShoppingCartCantProceedToCheckoutException.class);

        assertThat(shoppingCart).satisfies(
                sc -> assertThat(sc.isEmpty()).isTrue(),
                sc -> assertThat(sc.totalItems()).isEqualTo(Quantity.ZERO),
                sc -> assertThat(sc.totalAmount()).isEqualTo(Money.ZERO)
        );
    }

    @Test
    void givenShoppingCartWithUnavailableItems_whenCallCheckout_shouldThrowException() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        Product mousePadAvailable = ProductTestDataBuilder.aProductAltMousePad().build();

        shoppingCart.addItem(mousePadAvailable, Quantity.of(1));

        Product mousePadUnavailablee = ProductTestDataBuilder.aProductAltMousePad().inStock(false).build();

        shoppingCart.refreshItem(mousePadUnavailablee);

        assertThatThrownBy(() -> checkoutService.checkout(shoppingCart, null, null, null))
                .isInstanceOf(ShoppingCartCantProceedToCheckoutException.class);

        assertThat(shoppingCart).satisfies(
                sc -> assertThat(sc.isEmpty()).isTrue(),
                sc -> assertThat(sc.totalItems()).isEqualTo(Quantity.ZERO),
                sc -> assertThat(sc.totalAmount()).isEqualTo(Money.ZERO)
        );
    }

    @Test
    void givenShoppingCartWithAvailableItems_whenCallCheckout_shouldReturnOrder() {
        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .customerId(customerId).withItems(false).build();

        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Quantity quantity = Quantity.of(1);
        Product mousePad = ProductTestDataBuilder.aProductAltMousePad().build();
        shoppingCart.addItem(mousePad, quantity);

        ShoppingCartItem shoppingCartItem = shoppingCart.items().stream().findFirst().orElseThrow();

        Money expectedTotalAmount = shoppingCart.totalAmount().add(shipping.cost());
        Quantity expectedTotalItems = shoppingCart.totalItems();

        Order order = checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod);

        assertThat(shoppingCartItem).satisfies(
                sci -> assertThat(sci.productId()).isEqualTo(mousePad.id()),
                sci -> assertThat(sci.productName()).isEqualTo(mousePad.name()),
                sci -> assertThat(sci.quantity()).isEqualTo(quantity),
                sci -> assertThat(sci.price()).isEqualTo(mousePad.price())
        );

        assertThat(order).satisfies(
                o -> assertThat(o.customerId()).isEqualTo(customerId),
                o -> assertThat(o.status()).isEqualTo(OrderStatus.PLACED),
                o -> assertThat(o.totalItems()).isEqualTo(expectedTotalItems),
                o -> assertThat(o.totalAmount()).isEqualTo(expectedTotalAmount),

                o -> assertThat(o.billing()).isEqualTo(billing),
                o -> assertThat(o.shipping()).isEqualTo(shipping),
                o -> assertThat(o.paymentMethod()).isEqualTo(paymentMethod)
        );

        assertThat(shoppingCart).satisfies(
                sc -> assertThat(sc.isEmpty()).isTrue(),
                sc -> assertThat(sc.totalItems()).isEqualTo(Quantity.ZERO),
                sc -> assertThat(sc.totalAmount()).isEqualTo(Money.ZERO)
        );
    }
}