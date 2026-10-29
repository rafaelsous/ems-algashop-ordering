package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.order.saga;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PlaceOrderSagaPersistenceRepository extends JpaRepository<PlaceOrderSagaPersistence, UUID> {}
