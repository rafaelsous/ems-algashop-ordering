package com.rafaelsousa.algashop.ordering.infrastructure.persistence.assembler;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Order;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;

public class OrderPersistenceAssembler {

    public OrderPersistence fromDomain(Order order) {
        return merge(new OrderPersistence(), order);
    }

    public OrderPersistence merge(OrderPersistence orderPersistence, Order order) {
        orderPersistence.setId(order.id().value().toLong());
        orderPersistence.setCustomerId(order.customerId().value());
        orderPersistence.setTotalAmount(order.totalAmount().value());
        orderPersistence.setTotalItems(order.totalItems().value());
        orderPersistence.setStatus(order.status().name());
        orderPersistence.setPaymentMethod(order.paymentMethod().name());
        orderPersistence.setPlacedAt(order.placedAt());
        orderPersistence.setPaidAt(order.paidAt());
        orderPersistence.setCanceledAt(order.canceledAt());
        orderPersistence.setReadyAt(order.readyAt());

        return orderPersistence;
    }
}