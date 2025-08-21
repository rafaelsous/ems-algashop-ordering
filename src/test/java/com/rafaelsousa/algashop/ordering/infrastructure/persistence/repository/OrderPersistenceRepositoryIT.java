package com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository;

import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.HibernateConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.SpringDataAuditingConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer.CustomerPersistenceRepository;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.CustomerPersistenceTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.order.OrderPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceTestDataBuilder;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.order.OrderPersistenceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({SpringDataAuditingConfig.class, HibernateConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderPersistenceRepositoryIT {
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