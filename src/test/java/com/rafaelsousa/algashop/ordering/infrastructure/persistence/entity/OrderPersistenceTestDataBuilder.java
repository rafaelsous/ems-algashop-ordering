package com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity;

import com.rafaelsousa.algashop.ordering.domain.model.utils.IdGenerator;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence.OrderPersistenceBuilder;

import java.math.BigDecimal;
import java.util.Set;

public class OrderPersistenceTestDataBuilder {

    public static OrderPersistenceBuilder existingOrder() {
        return OrderPersistence.builder()
                .id(IdGenerator.generateTSID().toLong())
                .customerId(IdGenerator.generateTimeBasedUUID())
                .totalAmount(BigDecimal.valueOf(1250))
                .totalItems(3)
                .status("DRAFT")
                .paymentMethod("CREDIT_CARD")
                .items(Set.of(
                        existingItem().build(),
                        existingItemAlt().build()
                ));
    }

    private static OrderItemPersistence.OrderItemPersistenceBuilder existingItem() {
        return OrderItemPersistence.builder()
                .id(IdGenerator.generateTSID().toLong())
                .productId(IdGenerator.generateTimeBasedUUID())
                .productName("Notebook")
                .price(BigDecimal.valueOf(500))
                .quantity(2)
                .totalAmount(BigDecimal.valueOf(1000));
    }

    private static OrderItemPersistence.OrderItemPersistenceBuilder existingItemAlt() {
        return OrderItemPersistence.builder()
                .id(IdGenerator.generateTSID().toLong())
                .productId(IdGenerator.generateTimeBasedUUID())
                .productName("Mouse pad")
                .price(BigDecimal.valueOf(250))
                .quantity(1)
                .totalAmount(BigDecimal.valueOf(250));
    }
}