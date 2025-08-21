package com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer;

import com.rafaelsousa.algashop.ordering.domain.model.customer.Customer;
import com.rafaelsousa.algashop.ordering.domain.model.customer.Customers;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Email;
import com.rafaelsousa.algashop.ordering.domain.model.customer.CustomerId;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CustomersPersistenceProvider implements Customers {
    private final CustomerPersistenceRepository customerPersistenceRepository;
    private final CustomerPersistenceAssembler assembler;
    private final CustomerPersistenceDisassembler disassembler;
    private final EntityManager entityManager;

    @Override
    public Optional<Customer> ofId(CustomerId customerId) {
        Optional<CustomerPersistence> customerPersistenceOptional = customerPersistenceRepository
                .findById(customerId.value());

        return customerPersistenceOptional.map(disassembler::toDomain);
    }

    @Override
    public boolean exists(CustomerId customerId) {
        return customerPersistenceRepository.existsById(customerId.value());
    }

    @Override
    @Transactional
    public void add(Customer aggregateRoot) {
        UUID customerId = aggregateRoot.id().value();

        customerPersistenceRepository.findById(customerId).ifPresentOrElse(
                customerPersistence -> this.update(aggregateRoot, customerPersistence),
                () -> this.insert(aggregateRoot)
        );
    }

    @Override
    public long count() {
        return customerPersistenceRepository.count();
    }

    @Override
    public Optional<Customer> ofEmail(Email email) {
        return customerPersistenceRepository.findByEmail(email.value())
                .map(disassembler::toDomain);
    }

    @Override
    public boolean isEmailUnique(Email email, CustomerId exceptCustomerId) {
        return !customerPersistenceRepository.existsByEmailAndIdNot(email.value(), exceptCustomerId.value());
    }

    private void insert(Customer aggregateRoot) {
        CustomerPersistence customerPersistence = assembler.fromDomain(aggregateRoot);

        customerPersistenceRepository.saveAndFlush(customerPersistence);

        this.updateVersion(aggregateRoot, customerPersistence);
    }

    private void update(Customer aggregateRoot, CustomerPersistence customerPersistence) {
        customerPersistence = assembler.merge(customerPersistence, aggregateRoot);
        entityManager.detach(customerPersistence);

        customerPersistenceRepository.saveAndFlush(customerPersistence);

        this.updateVersion(aggregateRoot, customerPersistence);
    }

    @SneakyThrows
    @SuppressWarnings("java:S3011")
    private void updateVersion(Customer aggregateRoot, CustomerPersistence customerPersistence) {
        Field version = aggregateRoot.getClass().getDeclaredField("version");
        version.setAccessible(true);

        ReflectionUtils.setField(version, aggregateRoot, customerPersistence.getVersion());

        version.setAccessible(false);
    }
}