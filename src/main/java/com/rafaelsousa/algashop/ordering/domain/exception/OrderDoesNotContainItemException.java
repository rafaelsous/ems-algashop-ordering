package com.rafaelsousa.algashop.ordering.domain.exception;

import com.rafaelsousa.algashop.ordering.domain.valueobject.id.OrderId;
import com.rafaelsousa.algashop.ordering.domain.valueobject.id.OrderItemId;

public class OrderDoesNotContainItemException extends DomainException {
    public OrderDoesNotContainItemException(OrderId id, OrderItemId orderItemId) {
        super(ErrorMessages.ERROR_ORDER_DOES_NOT_CONTAIN_ITEM.formatted(id, orderItemId));
    }
}