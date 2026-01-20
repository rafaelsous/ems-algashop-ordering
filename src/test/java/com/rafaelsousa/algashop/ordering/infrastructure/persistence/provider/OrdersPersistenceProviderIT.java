package com.rafaelsousa.algashop.ordering.infrastructure.persistence.provider;

import com.rafaelsousa.algashop.ordering.core.domain.model.order.Order;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderId;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderStatus;
import com.rafaelsousa.algashop.ordering.core.domain.model.order.OrderTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.AbstractInfrastructureIT;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.HibernateConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomersPersistenceProvider;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.order.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@Import({
        OrdersPersistenceProvider.class,
        OrderPersistenceAssembler.class,
        OrderPersistenceDisassembler.class,
        SpringDataAuditingConfig.class,
        HibernateConfig.class,
        CustomersPersistenceProvider.class,
        CustomerPersistenceAssembler.class,
        CustomerPersistenceDisassembler.class
})
@TestPropertySource(properties = "spring.flyway.locations=classpath:db/migration,classpath:db/testdata")
class OrdersPersistenceProviderIT extends AbstractInfrastructureIT {
    private final OrdersPersistenceProvider ordersPersistenceProvider;
    private final OrderPersistenceRepository orderPersistenceRepository;

    @Autowired
    public OrdersPersistenceProviderIT(OrdersPersistenceProvider ordersPersistenceProvider,
                                       OrderPersistenceRepository orderPersistenceRepository) {
        this.ordersPersistenceProvider = ordersPersistenceProvider;
        this.orderPersistenceRepository = orderPersistenceRepository;
    }

    @Test
    void shouldUpdateAndKeepPersistenEntityState() {
        Order order = OrderTestDataBuilder.anOrder().status(OrderStatus.PLACED).build();
        long orderId = order.id().value().toLong();

        ordersPersistenceProvider.add(order);

        OrderPersistence orderPersistence = orderPersistenceRepository.findById(orderId).orElseThrow();

        assertThat(orderPersistence).satisfies(
                op -> assertThat(op.getStatus()).isEqualTo(OrderStatus.PLACED.name()),

                op -> assertThat(op.getCreatedByUserId()).isNotNull(),
                op -> assertThat(op.getLastModifiedByUserId()).isNotNull(),
                op -> assertThat(op.getLastModifiedAt()).isNotNull()
        );

        order = ordersPersistenceProvider.ofId(order.id()).orElseThrow();
        order.markAsPaid();
        ordersPersistenceProvider.add(order);

        orderPersistence = orderPersistenceRepository.findById(orderId).orElseThrow();

        assertThat(orderPersistence).satisfies(
                op -> assertThat(op.getStatus()).isEqualTo(OrderStatus.PAID.name()),

                op -> assertThat(op.getCreatedByUserId()).isNotNull(),
                op -> assertThat(op.getLastModifiedByUserId()).isNotNull(),
                op -> assertThat(op.getLastModifiedAt()).isNotNull()
        );
    }

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void shouldAddAndFindNotFailWhenNoTransaction() {
        Order order = OrderTestDataBuilder.anOrder().build();

        ordersPersistenceProvider.add(order);

        assertThatNoException().isThrownBy(() -> ordersPersistenceProvider.ofId(order.id()).orElseThrow());
    }

    @Test
    void shouldCountCorrectly() {
        long beforeCount = ordersPersistenceProvider.count();

        Order order = OrderTestDataBuilder.anOrder().build();
        ordersPersistenceProvider.add(order);

        long expectedCount = beforeCount + 1;
        assertThat(ordersPersistenceProvider.count()).isEqualTo(expectedCount);
    }

    @Test
    void shouldVerifyIfExists() {
        Order order = OrderTestDataBuilder.anOrder().build();
        OrderId orderId = order.id();

        assertThat(ordersPersistenceProvider.exists(orderId)).isFalse();

        ordersPersistenceProvider.add(order);

        assertThat(ordersPersistenceProvider.exists(orderId)).isTrue();
    }

    @Test
    void shouldUpdateVersionCorrectly() {
        Order order = OrderTestDataBuilder.anOrder().build();
        long orderId = order.id().value().toLong();

        ordersPersistenceProvider.add(order);

        OrderPersistence orderPersistence = orderPersistenceRepository.findById(orderId).orElseThrow();

        assertThat(orderPersistence.getVersion()).isZero();

        order = ordersPersistenceProvider.ofId(order.id()).orElseThrow();
        order.place();
        ordersPersistenceProvider.add(order);

        orderPersistence = orderPersistenceRepository.findById(orderId).orElseThrow();

        assertThat(orderPersistence.getVersion()).isEqualTo(1);
    }
}