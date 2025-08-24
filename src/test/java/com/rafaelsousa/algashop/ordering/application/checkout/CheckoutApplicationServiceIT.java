package com.rafaelsousa.algashop.ordering.application.checkout;

import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customers;
import com.rafaelsousa.algashop.ordering.domain.model.order.Order;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.domain.model.order.Orders;
import com.rafaelsousa.algashop.ordering.domain.model.order.shipping.ShippingCostService;
import com.rafaelsousa.algashop.ordering.domain.model.order.shipping.ShippingCostService.CalculationRequest;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.shoppingcart.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@Transactional
@SpringBootTest
class CheckoutApplicationServiceIT {
    private final Orders orders;
    private final Customers customers;
    private final ShoppingCarts shoppingCarts;
    private final CheckoutApplicationService checkoutApplicationService;

    @MockitoBean
    private ShippingCostService shippingCostService;

    @Autowired
    CheckoutApplicationServiceIT(Orders orders, Customers customers, ShoppingCarts shoppingCarts, CheckoutApplicationService checkoutApplicationService) {
        this.orders = orders;
        this.customers = customers;
        this.shoppingCarts = shoppingCarts;
        this.checkoutApplicationService = checkoutApplicationService;
    }

    @BeforeEach
    void setUp() {
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    void shouldCheckoutSuccessfully() {
        when(shippingCostService.calculate(any(CalculationRequest.class)))
                .thenReturn(ShippingCostService.CalculationResponse.builder()
                        .cost(Money.of("19.99"))
                        .expectedDeliveryDate(LocalDate.now().plusDays(7))
                        .build());

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(true).build();
        shoppingCarts.add(shoppingCart);

        CheckoutInput checkoutInput = CheckoutInputTestDataBuilder
                .aCheckoutInput().shoppingCartId(shoppingCart.id().value()).build();

        String orderIdString = checkoutApplicationService.checkout(checkoutInput);

        assertThat(orderIdString).isNotBlank();
        OrderId orderId = new OrderId(orderIdString);

        assertThat(orders.exists(orderId)).isTrue();

        Order order = orders.ofId(orderId).orElseThrow();

        assertThat(order).satisfies(
                o -> assertThat(order.id()).isEqualTo(orderId),
                o -> assertThat(order.customerId()).isEqualTo(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID),
                o -> assertThat(order.isPlaced()).isTrue(),
                o -> assertThat(order.placedAt()).isNotNull()
        );

        ShoppingCart updatedShoppingCart = shoppingCarts.ofId(shoppingCart.id()).orElseThrow();

        assertThat(updatedShoppingCart.isEmpty()).isTrue();
    }

    @Test
    void shouldThrowExceptionWhenTryingToCheckoutNonExistentShoppingCart() {
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(true).build();

        ShoppingCartId shoppingCartId = shoppingCart.id();
        CheckoutInput checkoutInput = CheckoutInputTestDataBuilder
                .aCheckoutInput().shoppingCartId(shoppingCartId.value()).build();

        assertThat(shoppingCarts.exists(shoppingCartId)).isFalse();

        assertThatThrownBy(() -> checkoutApplicationService.checkout(checkoutInput))
                .isInstanceOf(ShoppingCartNotFoundException.class)
                .hasMessageContaining(ErrorMessages.ERROR_SHOPPING_CART_NOT_FOUND.formatted(shoppingCartId.value()));
    }

    @Test
    void shouldThrowExceptionWhenTryingToCheckoutWithEmptyShoppingCart() {
        when(shippingCostService.calculate(any(CalculationRequest.class)))
                .thenReturn(ShippingCostService.CalculationResponse.builder()
                        .cost(Money.of("19.99"))
                        .expectedDeliveryDate(LocalDate.now().plusDays(7))
                        .build());

        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(false).build();
        shoppingCarts.add(shoppingCart);

        ShoppingCartId shoppingCartId = shoppingCart.id();
        CheckoutInput checkoutInput = CheckoutInputTestDataBuilder
                .aCheckoutInput().shoppingCartId(shoppingCartId.value()).build();

        assertThatThrownBy(() ->  checkoutApplicationService.checkout(checkoutInput))
                .isInstanceOf(ShoppingCartCantProceedToCheckoutException.class)
                .hasMessageContaining(ErrorMessages.ERROR_SHOPPING_CART_CANT_PROCEED_TO_CHECKOUT
                        .formatted(shoppingCartId.value()));
    }

    @Test
    void shouldThrowExceptionWhenTryingToCheckoutShoppingCartContainingUnavailableItems() {
        when(shippingCostService.calculate(any(CalculationRequest.class)))
                .thenReturn(ShippingCostService.CalculationResponse.builder()
                        .cost(Money.of("19.99"))
                        .expectedDeliveryDate(LocalDate.now().plusDays(7))
                        .build());

        Product ramMemoryAvailable = ProductTestDataBuilder.aProductAltRamMemory().build();
        ShoppingCart shoppingCart = ShoppingCartTestDataBuilder.aShoppingCart().withItems(true).build();
        shoppingCart.addItem(ramMemoryAvailable, Quantity.of(1));

        Product unavailableRamMemory = ProductTestDataBuilder.aProductAltRamMemory().inStock(false).build();

        shoppingCart.refreshItem(unavailableRamMemory);
        shoppingCarts.add(shoppingCart);

        ShoppingCartId shoppingCartId = shoppingCart.id();
        CheckoutInput checkoutInput = CheckoutInputTestDataBuilder
                .aCheckoutInput().shoppingCartId(shoppingCartId.value()).build();

        assertThatThrownBy(() ->  checkoutApplicationService.checkout(checkoutInput))
                .isInstanceOf(ShoppingCartCantProceedToCheckoutException.class)
                .hasMessageContaining(ErrorMessages.ERROR_SHOPPING_CART_CANT_PROCEED_TO_CHECKOUT
                        .formatted(shoppingCartId.value()));
    }
}