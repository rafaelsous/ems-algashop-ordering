package com.rafaelsousa.algashop.ordering.domain.model.order;

import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCart;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartCantProceedToCheckoutException;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartItem;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.ShoppingCartTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Year;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {
    private CheckoutService checkoutService;

    @Mock
    private Orders orders;

    @BeforeEach
    void setUp() {
        CustomerHaveFreeShippingSpecification specification = new CustomerHaveFreeShippingSpecification(
                orders,
                LoyaltyPoints.of(100),
                Quantity.of(2),
                LoyaltyPoints.of(1000)
        );

        checkoutService = new CheckoutService(specification);
    }

    @Test
    void givenEmptyShoppingCart_whenCallCheckout_shouldThrowException() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        assertThatThrownBy(() -> checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod))
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
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        shoppingCart.addItem(mousePadAvailable, Quantity.of(1));

        Product mousePadUnavailable = ProductTestDataBuilder.aProductAltMousePad().inStock(false).build();

        shoppingCart.refreshItem(mousePadUnavailable);

        assertThatThrownBy(() -> checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod))
                .isInstanceOf(ShoppingCartCantProceedToCheckoutException.class);

        Money expectedTotalAmountWithoutShippingCost = mousePadAvailable.price();

        assertThat(shoppingCart).satisfies(
                sc -> assertThat(sc.isEmpty()).isFalse(),
                sc -> assertThat(sc.items()).hasSize(1),
                sc -> assertThat(sc.totalAmount()).isEqualTo(expectedTotalAmountWithoutShippingCost)
        );
    }

    @Test
    void givenShoppingCartWithAvailableItems_whenCallCheckout_shouldReturnOrder() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();

        CustomerId customerId = customer.id();
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

        Order order = checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod);

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

    @Test
    void givenValidShoppingCartAndCustomerWithFreeShipping_whenCallCheckout_shouldReturnOrderWithFreeShipping() {
        when(orders.salesQuantityByCustomerInYear(any(CustomerId.class), any(Year.class)))
                .thenReturn(new Quantity(2));

        Customer customer = CustomerTestDataBuilder.existingCustomer().loyaltyPoints(LoyaltyPoints.of(3000)).build();

        CustomerId customerId = customer.id();
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart()
                .customerId(customerId).withItems(false).build();

        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Quantity quantity = Quantity.of(1);
        Product mousePad = ProductTestDataBuilder.aProductAltMousePad().build();
        shoppingCart.addItem(mousePad, quantity);

        ShoppingCartItem shoppingCartItem = shoppingCart.items().stream().findFirst().orElseThrow();

        Money expectedTotalAmount = shoppingCart.totalAmount();
        Quantity expectedTotalItems = shoppingCart.totalItems();

        Order order = checkoutService.checkout(customer, shoppingCart, billing, shipping, paymentMethod);

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
                o -> assertThat(o.shipping()).isEqualTo(shipping.toBuilder().cost(Money.ZERO).build()),
                o -> assertThat(o.paymentMethod()).isEqualTo(paymentMethod)
        );

        assertThat(shoppingCart).satisfies(
                sc -> assertThat(sc.isEmpty()).isTrue(),
                sc -> assertThat(sc.totalItems()).isEqualTo(Quantity.ZERO),
                sc -> assertThat(sc.totalAmount()).isEqualTo(Money.ZERO)
        );
    }
}