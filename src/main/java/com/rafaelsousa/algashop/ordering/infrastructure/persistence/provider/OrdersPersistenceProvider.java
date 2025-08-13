package com.rafaelsousa.algashop.ordering.infrastructure.persistence.provider;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Order;
import com.rafaelsousa.algashop.ordering.domain.model.repository.Orders;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrdersPersistenceProvider implements Orders {
    private final OrderPersistenceRepository orderPersistenceRepository;
    private final OrderPersistenceAssembler assembler;
    private final OrderPersistenceDisassembler disassembler;

    @Override
    public Optional<Order> ofId(OrderId orderId) {
        Optional<OrderPersistence> orderPersistenceOptional = orderPersistenceRepository.findById(orderId.value().toLong());

        return orderPersistenceOptional.map(disassembler::toDomain);
    }

    @Override
    public boolean exists(OrderId orderId) {
        return false;
    }

    @Override
    public void add(Order aggregateRoot) {
        long orderId = aggregateRoot.id().value().toLong();

        orderPersistenceRepository.findById(orderId).ifPresentOrElse(
                orderPersistence -> update(aggregateRoot, orderPersistence),
                () -> insert(aggregateRoot)
        );
    }

    private void insert(Order aggregateRoot) {
        OrderPersistence orderPersistence = assembler.fromDomain(aggregateRoot);
        orderPersistenceRepository.saveAndFlush(orderPersistence);
    }

    private void update(Order aggregateRoot, OrderPersistence orderPersistence) {
        orderPersistence = assembler.merge(orderPersistence, aggregateRoot);
        orderPersistenceRepository.saveAndFlush(orderPersistence);
    }

    @Override
    public int count() {
        return 0;
    }
}