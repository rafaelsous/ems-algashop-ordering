package com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository;

import com.rafaelsousa.algashop.ordering.domain.model.utils.IdGenerator;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;

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
        long orderPersistenceId = IdGenerator.generateTSID().toLong();
        OrderPersistence orderPersistence = OrderPersistence.builder()
                .id(orderPersistenceId)
                .customerId(IdGenerator.generateTimeBasedUUID())
                .totalAmount(BigDecimal.valueOf(1000))
                .totalItems(2)
                .status("DRAFT")
                .paymentMethod("CREDIT_CARD")
                .build();

        orderPersistenceRepository.saveAndFlush(orderPersistence);

        Assertions.assertThat(orderPersistenceRepository.existsById(orderPersistenceId)).isTrue();
    }

    @Test
    void shouldCount() {
        long ordersCount = orderPersistenceRepository.count();

        Assertions.assertThat(ordersCount).isZero();
    }
}