package com.rafaelsousa.algashop.ordering.domain.model.order;

import com.rafaelsousa.algashop.ordering.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;

public class OrderDoesNotContainItemException extends DomainException {
    public OrderDoesNotContainItemException(OrderId id, OrderItemId orderItemId) {
        super(ErrorMessages.ERROR_ORDER_DOES_NOT_CONTAIN_ITEM.formatted(id, orderItemId));
    }
}