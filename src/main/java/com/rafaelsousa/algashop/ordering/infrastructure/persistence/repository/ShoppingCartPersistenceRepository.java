package com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository;

import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartPersistenceRepository extends JpaRepository<ShoppingCartPersistence, UUID> {

    @Query("""
            SELECT sc
            FROM ShoppingCartPersistence sc
            WHERE sc.customer.id = :customerId
    """)
    Optional<ShoppingCartPersistence> findByCustomerId(@Param("customerId") UUID customerId);
}