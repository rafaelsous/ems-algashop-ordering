package com.rafaelsousa.algashop.ordering.infrastructure.persistence.provider;

import com.rafaelsousa.algashop.ordering.domain.model.entity.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.entity.Order;
import com.rafaelsousa.algashop.ordering.domain.model.entity.OrderStatus;
import com.rafaelsousa.algashop.ordering.domain.model.entity.OrderTestDataBuilder;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.HibernateConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.assembler.CustomerPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler.CustomerPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNoException;

@DataJpaTest
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
class OrdersPersistenceProviderIT {
    private final OrdersPersistenceProvider ordersPersistenceProvider;
    private final OrderPersistenceRepository orderPersistenceRepository;
    private final CustomersPersistenceProvider customersPersistenceProvider;

    @Autowired
    public OrdersPersistenceProviderIT(OrdersPersistenceProvider ordersPersistenceProvider,
                                       OrderPersistenceRepository orderPersistenceRepository,
                                       CustomersPersistenceProvider customersPersistenceProvider) {
        this.ordersPersistenceProvider = ordersPersistenceProvider;
        this.orderPersistenceRepository = orderPersistenceRepository;
        this.customersPersistenceProvider = customersPersistenceProvider;
    }

    @BeforeEach
    void setUp() {
        if (!customersPersistenceProvider.exists(CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID)) {
            customersPersistenceProvider.add(CustomerTestDataBuilder.existingCustomer().build());
        }
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
        assertThat(ordersPersistenceProvider.count()).isZero();

        Order order = OrderTestDataBuilder.anOrder().build();
        ordersPersistenceProvider.add(order);

        assertThat(ordersPersistenceProvider.count()).isEqualTo(1L);
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