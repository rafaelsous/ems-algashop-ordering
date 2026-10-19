package com.rafaelsousa.algashop.ordering.core.application.order.snapshot;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BillingSnapshot(
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank String document,
        @NotBlank String phone,
        @NotBlank String email,
        @NotNull @Valid AddressSnapshot address) {}
