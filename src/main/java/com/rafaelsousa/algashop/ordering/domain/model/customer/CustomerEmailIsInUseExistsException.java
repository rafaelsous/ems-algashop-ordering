package com.rafaelsousa.algashop.ordering.domain.model.customer;

import com.rafaelsousa.algashop.ordering.domain.model.DomainException;
import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.domain.model.commons.Email;

public class CustomerEmailIsInUseExistsException extends DomainException {
    public CustomerEmailIsInUseExistsException(Email email) {
        super(ErrorMessages.ERROR_CUSTOMER_EMAIL_IS_IN_USE.formatted(email.value()));
    }
}