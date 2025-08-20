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
    }

    @Test
    void givenShoppingCartWithUnavailableItems_whenCallCheckout_shouldThrowException() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        Product availableMousePad = ProductTestDataBuilder.aProductAltMousePad().build();

        shoppingCart.addItem(availableMousePad, Quantity.of(1));

        Product unavailableMousePad = ProductTestDataBuilder.aProductAltMousePad().inStock(false).build();

        shoppingCart.refreshItem(unavailableMousePad);

        assertThatThrownBy(() -> checkoutService.checkout(shoppingCart, null, null, null))
                .isInstanceOf(ShoppingCartCantProceedToCheckoutException.class);
    }

    @Test
    void givenShoppingCartWithAvailableItems_whenCallCheckout_shouldReturnOrder() {
        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .customerId(customerId).withItems(true).build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Money expectedTotalAmount = shoppingCart.totalAmount().add(shipping.cost());
        Quantity expectedTotalItems = shoppingCart.totalItems();

        Order order = checkoutService.checkout(shoppingCart, billing, shipping, paymentMethod);

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