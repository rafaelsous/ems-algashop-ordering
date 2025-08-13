package com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity;

import com.rafaelsousa.algashop.ordering.domain.model.utils.IdGenerator;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence.OrderPersistenceBuilder;

import java.math.BigDecimal;

public class OrderPersistenceTestDataBuilder {

    public static OrderPersistenceBuilder existingOrder() {
        return OrderPersistence.builder()
                .id(IdGenerator.generateTSID().toLong())
                .customerId(IdGenerator.generateTimeBasedUUID())
                .totalAmount(BigDecimal.valueOf(1000))
                .totalItems(2)
                .status("DRAFT")
                .paymentMethod("CREDIT_CARD");
    }
}