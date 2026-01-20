package com.rafaelsousa.algashop.ordering.core.domain.model.customer;

import com.rafaelsousa.algashop.ordering.core.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.core.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Email;

public class CustomerEmailAlreadyExistsException extends DomainException {
    public CustomerEmailAlreadyExistsException(Email email) {
        super(ErrorMessages.ERROR_CUSTOMER_EMAIL_IS_IN_USE.formatted(email.value()));
    }
}