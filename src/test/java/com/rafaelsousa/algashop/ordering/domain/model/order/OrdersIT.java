package com.rafaelsousa.algashop.ordering.domain.model.order;

import com.rafaelsousa.algashop.ordering.domain.model.customer.Customers;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.HibernateConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.order.OrderPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.order.OrderPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.order.OrdersPersistenceProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.time.Year;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import({
        OrdersPersistenceProvider.class,
        OrderPersistenceAssembler.class,
        OrderPersistenceDisassembler.class,
        HibernateConfig.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceAssembler.class,
        CustomerPersistenceDisassembler.class
})
class OrdersIT {
    private final Orders orders;
    private final Customers customers;

    @Autowired
    public OrdersIT(Orders orders, Customers customers) {
        this.orders = orders;
        this.customers = customers;
    }

    @BeforeEach
    void setUp() {
        if (!customers.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customers.add(CustomerTestDataBuilder.existingCustomer().build());
        }
    }

    @Test
    void shouldPersistAndFind() {
        Order originalOrder = OrderTestDataBuilder.anOrder().build();
        OrderId orderId = originalOrder.id();

        orders.add(originalOrder);
        Optional<Order> orderOptional = orders.ofId(orderId);

        assertThat(orderOptional).isPresent();

        Order savedOrder = orderOptional.get();

        assertThat(savedOrder).satisfies(
                so -> assertThat(so.id()).isEqualTo(orderId),
                so -> assertThat(so.customerId()).isEqualTo(originalOrder.customerId()),
                so -> assertThat(so.totalAmount()).isEqualTo(originalOrder.totalAmount()),
                so -> assertThat(so.totalItems()).isEqualTo(originalOrder.totalItems()),
                so -> assertThat(so.status()).isEqualTo(originalOrder.status()),
                so -> assertThat(so.paymentMethod()).isEqualTo(originalOrder.paymentMethod()),
                so -> assertThat(so.placedAt()).isEqualTo(originalOrder.placedAt()),
                so -> assertThat(so.paidAt()).isEqualTo(originalOrder.paidAt()),
                so -> assertThat(so.canceledAt()).isEqualTo(originalOrder.canceledAt())
        );
    }

    @Test
    void shouldUpdateExistingOrder() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();

        orders.add(order);

        order = orders.ofId(order.id()).orElseThrow();

        order.markAsPaid();

        orders.add(order);

        order = orders.ofId(order.id()).orElseThrow();

        assertThat(order.isPaid()).isTrue();
    }

    @Test
    void shouldNotAllowStaleUpdates() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        OrderId orderId = order.id();

        orders.add(order);

        Order orderT1 = orders.ofId(orderId).orElseThrow();
        Order orderT2 = orders.ofId(orderId).orElseThrow();

        orderT1.markAsPaid();
        orders.add(orderT1);

        orderT2.cancel();

        assertThatThrownBy(() -> orders.add(orderT2)).isInstanceOf(ObjectOptimisticLockingFailureException.class);

        Order savedOrder = orders.ofId(orderId).orElseThrow();

        assertThat(savedOrder).satisfies(
                so -> assertThat(so.paidAt()).isNotNull(),
                so -> assertThat(so.canceledAt()).isNull()
        );
    }

    @Test
    void shouldCountExistingOrders() {
        Order order = OrderTestDataBuilder.anOrder().build();

        assertThat(orders.count()).isZero();

        orders.add(order);

        assertThat(orders.count()).isEqualTo(1L);
    }

    @Test
    void shouldReturnIfOrderExists() {
        Order order = OrderTestDataBuilder.anOrder().build();

        assertThat(orders.exists(order.id())).isFalse();

        orders.add(order);

        assertThat(orders.exists(order.id())).isTrue();
        assertThat(orders.exists(new OrderId())).isFalse();
    }

    @Test
    void shouldListExistingOrdersByYear() {
        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        orders.add(OrderTestDataBuilder.anOrder()
                .status(OrderStatus.PLACED)
                .build());

        orders.add(OrderTestDataBuilder.anOrder()
                .status(OrderStatus.PLACED)
                .build());

        orders.add(OrderTestDataBuilder.anOrder()
                .status(OrderStatus.DRAFT)
                .build());

        orders.add(OrderTestDataBuilder.anOrder()
                .status(OrderStatus.CANCELED)
                .build());

        List<Order> orderList = orders.placedByCustomerInYear(customerId, Year.now());

        assertWith(orderList,
                o -> assertThat(o).isNotEmpty(),
                o -> assertThat(o).hasSize(2)
        );

        orderList = orders.placedByCustomerInYear(customerId, Year.now().minusYears(1));

        assertThat(orderList).isEmpty();

        orderList = orders.placedByCustomerInYear(new CustomerId(), Year.now());

        assertThat(orderList).isEmpty();
    }

    @Test
    void shouldReturnTotalSoldByCustomer() {
        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;

        Order order1 = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();
        Order order2 = OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build();

        orders.add(order1);
        orders.add(order2);

        orders.add(OrderTestDataBuilder.anOrder()
                .status(OrderStatus.PLACED)
                .build());

        orders.add(OrderTestDataBuilder.anOrder()
                .status(OrderStatus.CANCELED)
                .build());

        Money expectedTotalSold = order1.totalAmount().add(order2.totalAmount());

        assertThat(orders.totalSoldForCustomer(customerId)).isEqualTo(expectedTotalSold);
        assertThat(orders.totalSoldForCustomer(new CustomerId())).isEqualTo(Money.ZERO);
    }

    @Test
    void shouldReturnQuantityOrderedByCustomer() {
        CustomerId customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID;
        List<Order> ordersWithValidStates = List.of(
                OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build(),
                OrderTestDataBuilder.anOrder().status(OrderStatus.PAID).build()
        );

        ordersWithValidStates.forEach(orders::add);

        orders.add(OrderTestDataBuilder.anOrder()
                .status(OrderStatus.PLACED)
                .build());

        orders.add(OrderTestDataBuilder.anOrder()
                .status(OrderStatus.CANCELED)
                .build());

        Year currentYear = Year.now();
        assertThat(orders.salesQuantityByCustomerInYear(customerId, currentYear).value())
                .isEqualTo(ordersWithValidStates.size());
        assertThat(orders.salesQuantityByCustomerInYear(new CustomerId(), currentYear).value())
                .isZero();
        assertThat(orders.salesQuantityByCustomerInYear(customerId, currentYear.minusYears(1)).value())
                .isZero();
    }
}