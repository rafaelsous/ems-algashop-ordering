package com.rafaelsousa.algashop.ordering.core.domain.model.order;

import com.rafaelsousa.algashop.ordering.core.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.core.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.core.domain.model.customer.CustomerId;

public class OrderNotBelongsToCustomerException extends DomainException {

    public OrderNotBelongsToCustomerException(CustomerId customerId, OrderId orderId) {
        super(ErrorMessages.ERROR_ORDER_NOT_BELONGS_TO_CUSTOMER.formatted(customerId.value(), orderId.value()));
    }
}