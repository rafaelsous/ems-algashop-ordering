package com.rafaelsousa.algashop.ordering.domain.exception;

public class ErrorMessages {
    private ErrorMessages() { }

    public static final String VALIDATION_ERROR_EMAIL_IS_INVALID = "Email must not be null, blank or invalid";
    public static final String VALIDATION_ERROR_BIRTHDATE_MUST_IN_PAST = "Birthdate must be a past date";
    public static final String VALIDATION_ERROR_FULL_NAME_IS_NULL = "FullName cannot be null";
    public static final String VALIDATION_ERROR_FULL_NAME_IS_BLANK = "FullName cannot be blank";

    public static final String ERROR_CUSTOMER_ARCHIVED = "Customer is archived it cannot be changed";
}