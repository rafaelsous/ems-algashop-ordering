package com.rafaelsousa.algashop.ordering.domain.model.exception;

import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.OrderId;

public class OrderNotBelongsToCustomerException extends DomainException {

    public OrderNotBelongsToCustomerException(CustomerId customerId, OrderId orderId) {
        super(ErrorMessages.ERROR_ORDER_NOT_BELONGS_TO_CUSTOMER.formatted(customerId.value(), orderId.value()));
    }
}