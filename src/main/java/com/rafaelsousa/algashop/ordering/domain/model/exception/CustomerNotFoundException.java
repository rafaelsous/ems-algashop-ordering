package com.rafaelsousa.algashop.ordering.domain.model.exception;

import com.rafaelsousa.algashop.ordering.domain.model.valueobject.id.CustomerId;

public class CustomerNotFoundException extends DomainException {

    public CustomerNotFoundException(CustomerId customerId) {
        super(ErrorMessages.ERROR_CUSTOMER_NOT_FOUND.formatted(customerId));
    }
}