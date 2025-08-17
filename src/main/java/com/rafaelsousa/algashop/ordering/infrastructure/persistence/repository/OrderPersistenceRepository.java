package com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository;

import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface OrderPersistenceRepository extends JpaRepository<OrderPersistence, Long> {

    @Query("""
            SELECT o
                    FROM OrderPersistence o
                    WHERE o.customer.id = :customerId
                    AND YEAR(o.placedAt) = :year
            """)
    List<OrderPersistence> placedByCustomerIdInYear(
            @Param("customerId") UUID customerId,
            @Param("year") Integer year);
}