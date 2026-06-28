package com.rafaelsousa.algashop.ordering.core.domain.model.customer;

import com.rafaelsousa.algashop.ordering.core.domain.model.IdGenerator;

import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {

    public CustomerId {
        Objects.requireNonNull(value);
    }

    public CustomerId() {
        this(IdGenerator.generateTimeBasedUUID());
    }

    public static CustomerId of(UUID customerId) {
        return new CustomerId(customerId);
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}