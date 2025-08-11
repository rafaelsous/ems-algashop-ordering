package com.rafaelsousa.algashop.ordering.domain.model.exception;

import com.rafaelsousa.algashop.ordering.domain.model.entity.OrderStatus;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.OrderId;

public class OrderStatusCannotBeChangedException extends DomainException {

    public OrderStatusCannotBeChangedException(OrderId id, OrderStatus status, OrderStatus newStatus) {
        super(ErrorMessages.ERROR_ORDER_STATUS_CANNOT_BE_CHANGED.formatted(id.value(), status, newStatus));
    }
}