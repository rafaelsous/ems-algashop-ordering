package com.rafaelsousa.algashop.ordering.infrastructure.persistence.provider;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Order;
import com.rafaelsousa.algashop.ordering.domain.model.repository.Orders;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.assembler.OrderPersistenceAssembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler.OrderPersistenceDisassembler;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository.OrderPersistenceRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrdersPersistenceProvider implements Orders {
    private final OrderPersistenceRepository orderPersistenceRepository;
    private final OrderPersistenceAssembler assembler;
    private final OrderPersistenceDisassembler disassembler;
    private final EntityManager entityManager;

    @Override
    public Optional<Order> ofId(OrderId orderId) {
        Optional<OrderPersistence> orderPersistenceOptional = orderPersistenceRepository.findById(orderId.value().toLong());

        return orderPersistenceOptional.map(disassembler::toDomain);
    }

    @Override
    public boolean exists(OrderId orderId) {
        return orderPersistenceRepository.existsById(orderId.value().toLong());
    }

    @Override
    public void add(Order aggregateRoot) {
        long orderId = aggregateRoot.id().value().toLong();

        orderPersistenceRepository.findById(orderId)
                .ifPresentOrElse(
                        orderPersistence -> update(aggregateRoot, orderPersistence),
                        () -> insert(aggregateRoot)
                );
    }

    private void insert(Order aggregateRoot) {
        OrderPersistence orderPersistence = assembler.fromDomain(aggregateRoot);
        orderPersistenceRepository.saveAndFlush(orderPersistence);
        updateVersion(aggregateRoot, orderPersistence);
    }

    private void update(Order aggregateRoot, OrderPersistence orderPersistence) {
        orderPersistence = assembler.merge(orderPersistence, aggregateRoot);
        entityManager.detach(orderPersistence);

        orderPersistence = orderPersistenceRepository.saveAndFlush(orderPersistence);
        updateVersion(aggregateRoot, orderPersistence);
    }

    @SneakyThrows
    @SuppressWarnings("java:S3011")
    private void updateVersion(Order aggregateRoot, OrderPersistence orderPersistence) {
        Field version = aggregateRoot.getClass().getDeclaredField("version");
        version.setAccessible(true);

        ReflectionUtils.setField(version, aggregateRoot, orderPersistence.getVersion());

        version.setAccessible(false);
    }

    @Override
    public long count() {
        return orderPersistenceRepository.count();
    }
}