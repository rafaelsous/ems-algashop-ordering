package com.rafaelsousa.algashop.ordering.infrastructure.persistence.repository;

import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.ShoppingCartPersistence;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

public interface ShoppingCartPersistenceRepository extends JpaRepository<ShoppingCartPersistence, UUID> {

    @Query("""
            SELECT sc
            FROM ShoppingCartPersistence sc
            WHERE sc.customer.id = :customerId
    """)
    Optional<ShoppingCartPersistence> findByCustomerId(@Param("customerId") UUID customerId);

    @Modifying
    @Transactional
    @Query("""
            UPDATE
                ShoppingCartItemPersistence sci
            SET
                sci.price = :price,
                sci.totalAmount = :price * sci.quantity
            WHERE
                sci.productId = :productId
    """)
    void updateItemPrice(@Param("productId") UUID productId, @Param("price") BigDecimal price);

    @Modifying
    @Transactional
    @Query("""
            UPDATE
                ShoppingCartItemPersistence sci
            SET
                sci.available = :available
            WHERE
                sci.productId = :productId
    """)
    void updateItemAvailability(@Param("productId")UUID productId, @Param("available") boolean available);

    @Modifying
    @Transactional
    @Query("""
            UPDATE
                ShoppingCartPersistence sc
            SET
                sc.totalAmount = (
                    SELECT SUM(sci.totalAmount)
                    FROM ShoppingCartItemPersistence sci
                    WHERE sci.shoppingCart.id = sc.id
                )
            WHERE
                EXISTS (SELECT 1
                    FROM ShoppingCartItemPersistence sci2
                    WHERE sci2.shoppingCart.id = sc.id
                    AND sci2.productId = :productId)
    """)
    void recalculateTotalForCartsWithProduct(@Param("productId") UUID productId);
}