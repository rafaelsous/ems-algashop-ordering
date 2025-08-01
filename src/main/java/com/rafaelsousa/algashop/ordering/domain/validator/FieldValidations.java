package com.rafaelsousa.algashop.ordering.domain.validator;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

public class FieldValidations {
    private FieldValidations() { }

    public static void requiredValidEmail(String email) {
        requiredValidEmail(email, null);
    }

    public static void requiredNotBlank(String value) {
        requiredNotBlank(value, null);
    }

    public static void requiredNotBlank(String value, String errorMessage) {
        Objects.requireNonNull(value);

        if (value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    public static void requiredValidEmail(String email, String errorMessage) {
        Objects.requireNonNull(email, errorMessage);

        if (email.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }

        if (!EmailValidator.getInstance().isValid(email)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}