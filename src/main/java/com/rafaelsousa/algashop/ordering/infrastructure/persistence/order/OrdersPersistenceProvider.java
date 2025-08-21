package com.rafaelsousa.algashop.ordering.infrastructure.persistence.order;

import com.rafaelsousa.algashop.ordering.domain.model.order.Order;
import com.rafaelsousa.algashop.ordering.domain.model.order.Orders;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Money;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.order.OrderId;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.time.Year;
import java.util.List;
import java.util.Optional;

@Component
@Transactional(readOnly = true)
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
    @Transactional
    public void add(Order aggregateRoot) {
        long orderId = aggregateRoot.id().value().toLong();

        orderPersistenceRepository.findById(orderId)
                .ifPresentOrElse(
                        orderPersistence -> update(aggregateRoot, orderPersistence),
                        () -> insert(aggregateRoot)
                );
    }

    @Override
    public long count() {
        return orderPersistenceRepository.count();
    }

    @Override
    public List<Order> placedByCustomerInYear(CustomerId customerId, Year year) {
        List<OrderPersistence> orderPersistenceList = orderPersistenceRepository.placedByCustomerIdInYear(customerId.value(), year.getValue());

        return orderPersistenceList.stream().map(disassembler::toDomain).toList();
    }

    @Override
    public Quantity salesQuantityByCustomerInYear(CustomerId customerId, Year year) {
        return Quantity.of((int) orderPersistenceRepository.salesQuantityByCustomerIdInYear(customerId.value(), year.getValue()));
    }

    @Override
    public Money totalSoldForCustomer(CustomerId customerId) {
        return Money.of(orderPersistenceRepository.totalSoldForCustomerId(customerId.value()));
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
}