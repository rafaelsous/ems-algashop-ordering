package com.rafaelsousa.algashop.ordering.application.order;

import com.rafaelsousa.algashop.ordering.application.order.query.OrderDetailOutput;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customers;
import com.rafaelsousa.algashop.ordering.domain.model.order.Order;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.order.Orders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
class OrderQueryServiceIT {
    private final OrderQueryService orderQueryService;
    private final Orders orders;
    private final Customers customers;

    @Autowired
    OrderQueryServiceIT(OrderQueryService orderQueryService, Orders orders, Customers customers) {
        this.orderQueryService = orderQueryService;
        this.orders = orders;
        this.customers = customers;
    }

    @Test
    void shouldFindById() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        Order order = OrderTestDataBuilder.anOrder().customerId(customer.id()).build();
        orders.add(order);

        OrderDetailOutput orderDetailOutput = orderQueryService.findById(order.id().toString());

        assertThat(orderDetailOutput)
                .extracting(
                        OrderDetailOutput::getId,
                        OrderDetailOutput::getTotalAmount
                ).contains(
                        order.id().toString(),
                        order.totalAmount().value()
                );
    }
}