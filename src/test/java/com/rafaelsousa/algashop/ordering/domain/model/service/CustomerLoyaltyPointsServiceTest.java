package com.rafaelsousa.algashop.ordering.domain.model.service;

import com.rafaelsousa.algashop.ordering.domain.model.entity.*;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.LoyaltyPoints;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Product;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Quantity;
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