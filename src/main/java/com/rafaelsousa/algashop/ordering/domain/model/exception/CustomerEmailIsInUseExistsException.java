package com.rafaelsousa.algashop.ordering.domain.model.exception;

import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Email;

public class CustomerEmailIsInUseExistsException extends DomainException {
    public CustomerEmailIsInUseExistsException(Email email) {
        super(ErrorMessages.ERROR_CUSTOMER_EMAIL_IS_IN_USE.formatted(email.value()));
    }
}