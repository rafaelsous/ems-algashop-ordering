package com.rafaelsousa.algashop.ordering.application.customer.management;

import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.domain.model.customer.*;
import com.rafaelsousa.algashop.ordering.domain.model.order.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@Transactional
@SpringBootTest
class CustomerLoyaltyPointsApplicationServiceIT {
    private final Orders orders;
    private final Customers customers;
    private final CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService;

    @Autowired
    CustomerLoyaltyPointsApplicationServiceIT(
            Orders orders, Customers customers,
            CustomerLoyaltyPointsApplicationService customerLoyaltyPointsApplicationService) {
        this.orders = orders;
        this.customers = customers;
        this.customerLoyaltyPointsApplicationService = customerLoyaltyPointsApplicationService;
    }

    @Test
    void shouldAccumulatePoints() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder().customerId(customer.id()).status(OrderStatus.READY).build();
        orders.add(order);

        customerLoyaltyPointsApplicationService.addLoyaltyPoints(customer.id().value(), order.id().toString());

        customer = customers.ofId(customer.id()).orElseThrow();

        LoyaltyPoints expectedLoyaltyPointsTotal = customerLoyaltyPointsApplicationService.getCalculatedLoyaltyPoints(order);

        assertThat(customer.loyaltyPoints()).isEqualTo(expectedLoyaltyPointsTotal);
    }

    @Test
    void shouldThrowExceptionWhenTryingToAddPointsUsingNonExistentCustomer() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder().customerId(customer.id()).status(OrderStatus.READY).build();
        orders.add(order);

        UUID inexistingRawCustomerId = new CustomerId().value();
        String rawOrderId = order.id().toString();

        assertThatThrownBy(() -> customerLoyaltyPointsApplicationService.addLoyaltyPoints(inexistingRawCustomerId, rawOrderId))
                .isInstanceOf(CustomerNotFoundException.class)
                .hasMessage(ErrorMessages.ERROR_CUSTOMER_NOT_FOUND.formatted(inexistingRawCustomerId));

        customer = customers.ofId(customer.id()).orElseThrow();

        assertThat(customer.loyaltyPoints()).isEqualTo(LoyaltyPoints.ZERO);
    }

    @Test
    void shouldThrowExceptionWhenTryingToAddPointsUsingNonExistentOrder() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        UUID rawCustomerId = customer.id().value();
        String inexistingRawOrderId = new OrderId().toString();

        assertThatThrownBy(() -> customerLoyaltyPointsApplicationService.addLoyaltyPoints(rawCustomerId, inexistingRawOrderId))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage(ErrorMessages.ERROR_ORDER_NOT_FOUND.formatted(inexistingRawOrderId));

        customer = customers.ofId(customer.id()).orElseThrow();

        assertThat(customer.loyaltyPoints()).isEqualTo(LoyaltyPoints.ZERO);
    }
}