package com.rafaelsousa.algashop.ordering.domain.model.order;

import com.rafaelsousa.algashop.ordering.domain.model.DomainEntityNotFoundException;
import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;

public class OrderNotFoundException extends DomainEntityNotFoundException {

    public OrderNotFoundException(OrderId orderId) {
        super(ErrorMessages.ERROR_ORDER_NOT_FOUND.formatted(orderId.toString()));
    }
}