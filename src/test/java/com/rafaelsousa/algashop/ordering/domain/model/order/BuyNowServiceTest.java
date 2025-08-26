package com.rafaelsousa.algashop.ordering.domain.model.order;

import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.customer.LoyaltyPoints;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductOutOfStockException;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductTestDataBuilder;
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
class BuyNowServiceTest {

    @Mock
    private Orders orders;

    private BuyNowService buyNowService;

    @BeforeEach
    void setUp() {
        CustomerHaveFreeShippingSpecification specification = new CustomerHaveFreeShippingSpecification(
                orders,
                100,
                2,
                1000
        );

        buyNowService = new BuyNowService(specification);
    }

    @Test
    void givenUnavailableProduct_whenCallBuyNow_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Product productUnavailable = ProductTestDataBuilder.aProductUnavailable().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity quantity = Quantity.of(1);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        assertThatThrownBy(() -> buyNowService.buyNow(productUnavailable, customer, billing,
                shipping, quantity, paymentMethod)).isInstanceOf(ProductOutOfStockException.class);
    }

    @Test
    void givenInvalidItemQuantity_whenCallBuyNow_shouldThrowException() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Product product = ProductTestDataBuilder.aProduct().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity invalidQuantity = Quantity.ZERO;
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        assertThatThrownBy(() -> buyNowService.buyNow(product, customer, billing, shipping,
                invalidQuantity, paymentMethod)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void givenShoppingCartWithAvailableItems_whenCallCheckout_shouldReturnOrder() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Product product = ProductTestDataBuilder.aProduct().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity quantity = Quantity.of(1);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Order order = buyNowService.buyNow(product, customer, billing, shipping, quantity, paymentMethod);

        OrderItem orderItem = order.items().iterator().next();
        Quantity expectedTotalItems = orderItem.quantity();
        Money expectedTotalAmount = product.price().add(shipping.cost());

        assertThat(order).satisfies(
                o -> assertThat(o).isNotNull(),
                o -> assertThat(o.id()).isNotNull(),
                o -> assertThat(o.customerId()).isEqualTo(customer.id()),
                o -> assertThat(o.status()).isEqualTo(OrderStatus.PLACED),
                o -> assertThat(o.totalItems()).isEqualTo(expectedTotalItems),
                o -> assertThat(o.totalAmount()).isEqualTo(expectedTotalAmount),

                o -> assertThat(o.billing()).isEqualTo(billing),
                o -> assertThat(o.shipping()).isEqualTo(shipping),
                o -> assertThat(o.paymentMethod()).isEqualTo(paymentMethod)
        );

        assertThat(orderItem).satisfies(
                oi -> assertThat(oi.productId()).isEqualTo(product.id()),
                oi -> assertThat(oi.productName()).isEqualTo(product.name()),
                oi -> assertThat(oi.quantity()).isEqualTo(quantity),
                oi -> assertThat(oi.price()).isEqualTo(product.price())
        );
    }

    @Test
    void givenCustomerWithFreeShipping_whenCallCheckout_shouldReturnOrderWithFreeShipping() {
        when(orders.salesQuantityByCustomerInYear(any(CustomerId.class), any(Year.class)))
                .thenReturn(new Quantity(2));

        Customer customer = CustomerTestDataBuilder.existingCustomer().loyaltyPoints(new LoyaltyPoints(100)).build();
        Product product = ProductTestDataBuilder.aProduct().build();
        Billing billing = OrderTestDataBuilder.aBilling();
        Shipping shipping = OrderTestDataBuilder.aShipping();
        Quantity quantity = Quantity.of(1);
        PaymentMethod paymentMethod = PaymentMethod.CREDIT_CARD;

        Order order = buyNowService.buyNow(product, customer, billing, shipping, quantity, paymentMethod);

        OrderItem orderItem = order.items().iterator().next();
        Quantity expectedTotalItems = orderItem.quantity();
        Money expectedTotalAmountWithFreeShipping = product.price().multiply(quantity);

        assertThat(order).satisfies(
                o -> assertThat(o).isNotNull(),
                o -> assertThat(o.id()).isNotNull(),
                o -> assertThat(o.customerId()).isEqualTo(customer.id()),
                o -> assertThat(o.status()).isEqualTo(OrderStatus.PLACED),
                o -> assertThat(o.totalItems()).isEqualTo(expectedTotalItems),
                o -> assertThat(o.totalAmount()).isEqualTo(expectedTotalAmountWithFreeShipping),

                o -> assertThat(o.billing()).isEqualTo(billing),
                o -> assertThat(o.shipping()).isEqualTo(shipping.toBuilder().cost(Money.ZERO).build()),
                o -> assertThat(o.paymentMethod()).isEqualTo(paymentMethod)
        );

        assertThat(orderItem).satisfies(
                oi -> assertThat(oi.productId()).isEqualTo(product.id()),
                oi -> assertThat(oi.productName()).isEqualTo(product.name()),
                oi -> assertThat(oi.quantity()).isEqualTo(quantity),
                oi -> assertThat(oi.price()).isEqualTo(product.price())
        );
    }
}