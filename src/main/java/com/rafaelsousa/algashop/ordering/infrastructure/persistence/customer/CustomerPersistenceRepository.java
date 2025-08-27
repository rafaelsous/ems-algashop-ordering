package com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CustomerPersistenceRepository
        extends JpaRepository<CustomerPersistence, UUID>,
        CustomerPersistenceQueries {
    Optional<CustomerPersistence> findByEmail(String value);
    boolean existsByEmailAndIdNot(String email, UUID customerId);
}