package com.rafaelsousa.algashop.ordering.infrastructure.persistence.customer;

import com.rafaelsousa.algashop.ordering.application.customer.query.CustomerOutput;

import java.util.Optional;
import java.util.UUID;

public interface CustomerPersistenceQueries {
    Optional<CustomerOutput> findByIdAsOutput(UUID customerId);
}