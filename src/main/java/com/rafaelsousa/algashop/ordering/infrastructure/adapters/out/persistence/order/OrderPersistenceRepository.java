package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.order;

import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderPersistenceRepository extends JpaRepository<OrderPersistence, Long> {

    @Query(
            """
            SELECT o
            FROM OrderPersistence o
            WHERE o.customer.id = :customerId
            AND YEAR(o.placedAt) = :year
    """)
    List<OrderPersistence> placedByCustomerIdInYear(
            @Param("customerId") UUID customerId, @Param("year") Integer year);

    @Query(
            """
            SELECT COUNT(o)
            FROM OrderPersistence o
            WHERE o.customer.id = :customerId
            AND YEAR(o.placedAt) = :year
            AND o.paidAt IS NOT NULL
            AND o.canceledAt IS NULL
    """)
    long salesQuantityByCustomerIdInYear(
            @Param("customerId") UUID customerId, @Param("year") Integer year);

    @Query(
            """
            SELECT COALESCE(SUM(o.totalAmount), 0)
            FROM OrderPersistence o
            WHERE o.customer.id = :customerId
            AND o.canceledAt IS NULL
            AND o.paidAt IS NOT NULL
    """)
    BigDecimal totalSoldForCustomerId(@Param("customerId") UUID customerId);

    @Override
    @EntityGraph(attributePaths = {"customer", "items"})
    Optional<OrderPersistence> findById(@NonNull Long id);

    @EntityGraph(attributePaths = {"customer", "items"})
//    @Query("SELECT o FROM OrderPersistence o WHERE o.id = :orderId AND o.customer.id = :customerId")
    Optional<OrderPersistence> findByIdAndCustomer_Id(@Param("orderId") Long orderId, @Param("customerId") UUID customerId);
}
