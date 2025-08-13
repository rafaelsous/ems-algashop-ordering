package com.rafaelsousa.algashop.ordering.infrastructure.persistence.disassembler;

import com.rafaelsousa.algashop.ordering.domain.model.entity.Order;
import com.rafaelsousa.algashop.ordering.domain.model.entity.OrderStatus;
import com.rafaelsousa.algashop.ordering.domain.model.entity.PaymentMethod;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Money;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Quantity;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.OrderId;
import com.rafaelsousa.algashop.ordering.infrastructure.persistence.entity.OrderPersistence;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class OrderPersistenceDisassembler {

    public Order toDomain(OrderPersistence orderPersistence) {
        return Order.existing()
                .id(new OrderId(orderPersistence.getId()))
                .customerId(new CustomerId(orderPersistence.getCustomerId()))
                .totalAmount(Money.of(orderPersistence.getTotalAmount()))
                .totalItems(Quantity.of(orderPersistence.getTotalItems()))
                .paymentMethod(PaymentMethod.valueOf(orderPersistence.getPaymentMethod()))
                .placedAt(orderPersistence.getPlacedAt())
                .paidAt(orderPersistence.getPlacedAt())
                .readyAt(orderPersistence.getReadyAt())
                .canceledAt(orderPersistence.getCanceledAt())
                .status(OrderStatus.valueOf(orderPersistence.getStatus()))
                .items(new HashSet<>())
                .version(orderPersistence.getVersion())
                .build();
    }
}