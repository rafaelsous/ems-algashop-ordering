package com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository;

import com.rafaelsousa.algashop.ordering.infrastructure.persistence.HibernateConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.config.SpringDataAuditingConfig;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceTestDataBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({SpringDataAuditingConfig.class, HibernateConfig.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class OrderPersistenceRepositoryIT {
    private final OrderPersistenceRepository orderPersistenceRepository;

    @Autowired
    public OrderPersistenceRepositoryIT(OrderPersistenceRepository orderPersistenceRepository) {
        this.orderPersistenceRepository = orderPersistenceRepository;
    }

    @Test
    void shouldPersist() {
        OrderPersistence orderPersistence = OrderPersistenceTestDataBuilder.existingOrder().build();

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
        OrderPersistence orderPersistence = OrderPersistenceTestDataBuilder.existingOrder().build();

        orderPersistence = orderPersistenceRepository.saveAndFlush(orderPersistence);

        assertThat(orderPersistence).satisfies(
                sop -> assertThat(sop.getCreatedByUserId()).isNotNull(),
                sop -> assertThat(sop.getLastModifiedByUserId()).isNotNull(),
                sop -> assertThat(sop.getLastModifiedAt()).isNotNull()
        );
    }
}