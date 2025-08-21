package com.rafaelsousa.algashop.ordering.domain.model.commons;

import com.rafaelsousa.algashop.ordering.domain.model.ErrorMessages;
import com.rafaelsousa.algashop.ordering.domain.model.FieldValidations;

public record Email(String value) {

    public Email(String value) {
        FieldValidations.requiredValidEmail(value, ErrorMessages.VALIDATION_ERROR_EMAIL_IS_INVALID);

        this.value = value.trim();
    }

    public static Email of(String value) {
        return new Email(value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}