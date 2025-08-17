package com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository;

import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.CustomerPersistence;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CustomerPersistenceRepository extends JpaRepository<CustomerPersistence, UUID> {
}