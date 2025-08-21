package com.rafaelsousa.algashop.ordering.domain.model.customer;

import com.rafaelsousa.algashop.ordering.domain.model.order.Order;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderStatus;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.product.Product;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.product.ProductTestDataBuilder;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerLoyaltyPointsServiceTest {

    CustomerLoyaltyPointsService customerLoyaltyPointsService = new CustomerLoyaltyPointsService();

    @Test
    void givenValidCustomerAndOrder_whenAddingPoints_shouldAccumulate() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.READY).build();

        customerLoyaltyPointsService.addPoints(customer, order);

        assertThat(customer.loyaltyPoints()).isEqualTo(new LoyaltyPoints(375));
    }

    @Test
    void givenValidCustomerAndOrderWithLowTotalAmount_whenAddingPoints_shouldNotAccumulate() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        Product mousePad = ProductTestDataBuilder.aProductAltMousePad().build();
        Order order = OrderTestDataBuilder.anOrder()
                .status(OrderStatus.DRAFT)
                .withItems(false)
                .build();

        order.addItem(mousePad, Quantity.of(1));

        order.place();
        order.markAsPaid();
        order.markAsReady();
        customerLoyaltyPointsService.addPoints(customer, order);

        assertThat(customer.loyaltyPoints()).isEqualTo(LoyaltyPoints.ZERO);
    }
}