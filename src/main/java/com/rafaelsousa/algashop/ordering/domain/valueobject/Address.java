package com.rafaelsousa.algashop.ordering.domain.valueobject;

import com.rafaelsousa.algashop.ordering.domain.validator.FieldValidations;
import lombok.Builder;

import java.util.Objects;

public record Address(
        String street,
        String complement,
        String neighborhood,
        String number,
        String city,
        String state,
        ZipCode zipCode
) {

    @Builder(toBuilder = true)
    public Address {
        FieldValidations.requiredNotBlank(street);
        FieldValidations.requiredNotBlank(neighborhood);
        FieldValidations.requiredNotBlank(number);
        FieldValidations.requiredNotBlank(city);
        FieldValidations.requiredNotBlank(state);
        Objects.requireNonNull(zipCode);
    }
}