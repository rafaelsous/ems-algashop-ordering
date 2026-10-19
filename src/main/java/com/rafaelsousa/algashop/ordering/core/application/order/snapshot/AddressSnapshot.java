package com.rafaelsousa.algashop.ordering.core.application.order.snapshot;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AddressSnapshot(
        @NotBlank String street,
        String complement,
        @NotBlank String neighborhood,
        @NotBlank String number,
        @NotBlank String city,
        @NotBlank String state,
        @NotBlank @Size(min = 5, max = 5) String zipCode) {}
