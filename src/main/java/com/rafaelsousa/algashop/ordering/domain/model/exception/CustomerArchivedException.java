package com.rafaelsousa.algashop.ordering.domain.model.exception;

public class CustomerArchivedException extends DomainException {

    public CustomerArchivedException() {
        super(ErrorMessages.ERROR_CUSTOMER_ARCHIVED);
    }

    public CustomerArchivedException(Throwable cause) {
        super(ErrorMessages.ERROR_CUSTOMER_ARCHIVED, cause);
    }
}