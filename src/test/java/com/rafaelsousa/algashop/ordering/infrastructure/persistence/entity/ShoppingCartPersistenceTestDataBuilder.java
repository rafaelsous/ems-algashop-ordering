package com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity;

import com.rafaelsousa.algashop.ordering.domain.model.IdGenerator;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartItemPersistence;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.shoppingcart.ShoppingCartPersistence;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public class ShoppingCartPersistenceTestDataBuilder {

    public static ShoppingCartPersistence.ShoppingCartPersistenceBuilder existingShoppingCart() {
        return ShoppingCartPersistence.builder()
                .id(IdGenerator.generateTimeBasedUUID())
                .customer(CustomerPersistenceTestDataBuilder.aCustomer().build())
                .totalAmount(BigDecimal.valueOf(1250))
                .totalItems(3)
                .createdAt(OffsetDateTime.now())
                .items(Set.of(
                        existingItem().build(),
                        existingItemAlt().build()
                ));
    }

    private static ShoppingCartItemPersistence.ShoppingCartItemPersistenceBuilder existingItem() {
        return ShoppingCartItemPersistence.builder()
                .id(IdGenerator.generateTimeBasedUUID())
                .productId(IdGenerator.generateTimeBasedUUID())
                .productName("Notebook")
                .price(BigDecimal.valueOf(500))
                .quantity(2)
                .available(true)
                .totalAmount(BigDecimal.valueOf(1000));
    }

    private static ShoppingCartItemPersistence.ShoppingCartItemPersistenceBuilder existingItemAlt() {
        return ShoppingCartItemPersistence.builder()
                .id(IdGenerator.generateTimeBasedUUID())
                .productId(IdGenerator.generateTimeBasedUUID())
                .productName("Mouse pad")
                .price(BigDecimal.valueOf(250))
                .quantity(1)
                .available(true)
                .totalAmount(BigDecimal.valueOf(250));
    }
}