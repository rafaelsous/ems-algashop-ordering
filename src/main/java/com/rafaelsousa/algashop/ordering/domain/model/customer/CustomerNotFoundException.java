package com.rafaelsousa.algashop.ordering.domain.model.customer;

import com.rafaelsousa.algashop.ordering.domain.model.DomainEntityNotFoundException;
import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;

public class CustomerNotFoundException extends DomainEntityNotFoundException {

    public CustomerNotFoundException(CustomerId customerId) {
        super(ErrorMessages.ERROR_CUSTOMER_NOT_FOUND.formatted(customerId));
    }
}