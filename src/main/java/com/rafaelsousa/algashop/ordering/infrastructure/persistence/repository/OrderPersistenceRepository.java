package com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository;

import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderPersistenceRepository extends JpaRepository<OrderPersistence, Long> {
}