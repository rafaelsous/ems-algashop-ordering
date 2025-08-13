package com.rafaelsousa.algashop.ordering.infrastructure.persistence.provider;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Order;
import com.rafaelsousa.algashop.ordering.domain.model.repository.Orders;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrdersPersistenceProvider implements Orders {
    private final OrderPersistenceRepository orderPersistenceRepository;

    @Override
    public Optional<Order> ofId(OrderId orderId) {
        return Optional.empty();
    }

    @Override
    public boolean exists(OrderId orderId) {
        return false;
    }

    @Override
    public void add(Order aggregateRoot) {
        OrderPersistence orderPersistence = OrderPersistence.builder()
                .id(aggregateRoot.id().value().toLong())
                .customerId(aggregateRoot.customerId().value())
                .build();

        orderPersistenceRepository.saveAndFlush(orderPersistence);
    }

    @Override
    public int count() {
        return 0;
    }
}