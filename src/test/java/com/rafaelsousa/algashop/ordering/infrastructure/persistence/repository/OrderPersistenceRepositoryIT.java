package com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository;

import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistenceTestDataBuilder;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
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

        Assertions.assertThat(orderPersistenceRepository.existsById(orderPersistence.getId())).isTrue();
    }

    @Test
    void shouldCount() {
        long ordersCount = orderPersistenceRepository.count();

        Assertions.assertThat(ordersCount).isZero();
    }
}