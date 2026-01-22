package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.order;

import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.AbstractInfrastructureIT;
import com.rafaelsousa.algashop.ordering.infrastructure.config.hibernate.HibernateConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.config.auditing.SpringDataAuditingConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceRepository;
import com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.customer.CustomerPersistenceTestDataBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Import({SpringDataAuditingConfig.class, HibernateConfig.class})
class OrderPersistenceRepositoryIT extends AbstractInfrastructureIT {
    private final OrderPersistenceRepository orderPersistenceRepository;
    private final CustomerPersistenceRepository customerPersistenceRepository;

    private CustomerPersistence customerPersistence;

    @Autowired
    public OrderPersistenceRepositoryIT(OrderPersistenceRepository orderPersistenceRepository, CustomerPersistenceRepository customerPersistenceRepository) {
        this.orderPersistenceRepository = orderPersistenceRepository;
        this.customerPersistenceRepository = customerPersistenceRepository;
    }

    @BeforeEach
    void setUp() {
        UUID customerId = CustomerTestDataBuilder.DEFAULT_CUSTOMER_ID.value();

        if (!customerPersistenceRepository.existsById(customerId)) {
            customerPersistence = customerPersistenceRepository.saveAndFlush(CustomerPersistenceTestDataBuilder.aCustomer().build());
        }
    }

    @Test
    void shouldPersist() {
        OrderPersistence orderPersistence = OrderPersistenceTestDataBuilder.existingOrder()
                .customer(customerPersistence)
                .build();

        orderPersistenceRepository.saveAndFlush(orderPersistence);

        assertThat(orderPersistenceRepository.existsById(orderPersistence.getId())).isTrue();

        OrderPersistence savedOrderPersistence = orderPersistenceRepository.findById(orderPersistence.getId()).orElseThrow();

        assertThat(savedOrderPersistence.getItems()).isNotEmpty();
    }

    @Test
    void shouldCount() {
        long ordersCount = orderPersistenceRepository.count();

        assertThat(ordersCount).isZero();
    }

    @Test
    void shouldSetAuditingValues() {
        OrderPersistence orderPersistence = OrderPersistenceTestDataBuilder.existingOrder()
                .customer(customerPersistence)
                .build();

        orderPersistence = orderPersistenceRepository.saveAndFlush(orderPersistence);

        assertThat(orderPersistence).satisfies(
                sop -> assertThat(sop.getCreatedByUserId()).isNotNull(),
                sop -> assertThat(sop.getLastModifiedByUserId()).isNotNull(),
                sop -> assertThat(sop.getLastModifiedAt()).isNotNull()
        );
    }
}