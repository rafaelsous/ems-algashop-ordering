package com.rafaelsousa.algashop.ordering.application.order;

import com.rafaelsousa.algashop.ordering.application.order.query.OrderDetailOutput;
import com.rafaelsousa.algashop.ordering.application.order.query.OrderQueryService;
import com.rafaelsousa.algashop.ordering.application.order.query.OrderSummaryOutput;
import com.rafaelsousa.algashop.ordering.application.utility.PageFilter;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customers;
import com.rafaelsousa.algashop.ordering.domain.model.order.Order;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderStatus;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.order.Orders;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
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

    @Test
    void shouldFilterByPage() {
        Customer customer = CustomerTestDataBuilder.existingCustomer().build();
        customers.add(customer);

        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.DRAFT).withItems(false).customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.READY).customerId(customer.id()).build());
        orders.add(OrderTestDataBuilder.anOrder().status(OrderStatus.CANCELED).customerId(customer.id()).build());

        Page<OrderSummaryOutput> page = orderQueryService.filter(PageFilter.of(3, 0));

        assertThat(page.getTotalPages()).isEqualTo(2);
        assertThat(page.getTotalElements()).isEqualTo(5);
        assertThat(page.getNumberOfElements()).isEqualTo(3);
    }
}