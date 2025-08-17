package com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository;

import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderPersistenceRepository extends JpaRepository<OrderPersistence, Long> {
    List<OrderPersistence> findByCustomer_IdAndPlacedAtBetween(UUID customerId, OffsetDateTime start, OffsetDateTime end);
}