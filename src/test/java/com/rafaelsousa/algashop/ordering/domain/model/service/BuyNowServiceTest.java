package com.rafaelsousa.algashop.ordering.domain.model.service;

import com.rafaelsousa.algashop.ordering.domain.model.entity.*;
import com.rafaelsousa.algashop.ordering.domain.model.exception.ProductOutOfStockException;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.*;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

class BuyNowServiceTest {
    private final BuyNowService buyNowService = new BuyNowService();

    @Test
    void givenUnavailableProduct_whenCallBuyNow_shouldThrowException() {
        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
        Product productUnavailable = ProductTestDataBuilder.aProductUnavailable().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity quantity = Quantity.of(1);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        assertThatThrownBy(() -> buyNowService.buyNow(productUnavailable, customerId, billing,
                shipping, quantity, paymentMethod)).isInstanceOf(ProductOutOfStockException.class);
    }

    @Test
    void givenInvalidItemQuantity_whenCallBuyNow_shouldThrowException() {
        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
        Product product = ProductTestDataBuilder.aProduct().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity invalidQuantity = Quantity.ZERO;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        assertThatThrownBy(() -> buyNowService.buyNow(product, customerId, billing, shipping,
                invalidQuantity, paymentMethod)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenShoppingCartWithAvailableItems_whenCallCheckout_shouldReturnOrder() {
        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
        Product product = ProductTestDataBuilder.aProduct().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity quantity = Quantity.of(1);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Order order = buyNowService.buyNow(product, customerId, billing, shipping, quantity, paymentMethod);

        Quantity expectedTotalItems = order.items().iterator().next().quantity();
        Money expectedTotalAmount = product.price().add(shipping.cost());

        assertThat(order).satisfies(
                o -> assertThat(o).isNotNull(),
                o -> assertThat(o.id()).isNotNull(),
                o -> assertThat(o.customerId()).isEqualTo(customerId),
                o -> assertThat(o.status()).isEqualTo(OrderStatus.PLACED),
                o -> assertThat(o.totalItems()).isEqualTo(expectedTotalItems),
                o -> assertThat(o.totalAmount()).isEqualTo(expectedTotalAmount),

                o -> assertThat(o.billing()).isEqualTo(billing),
                o -> assertThat(o.shipping()).isEqualTo(shipping),
                o -> assertThat(o.paymentMethod()).isEqualTo(paymentMethod)
        );
    }
}