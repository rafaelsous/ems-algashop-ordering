package com.rafaelsousa.algashop.ordering.domain.valueobject;

import com.rafaelsousa.algashop.ordering.domain.utils.IdGenerator;

import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {
    public CustomerId() {
        this(IdGenerator.generateTimeBasedUUID());
    }

    public CustomerId(UUID value) {
        Objects.requireNonNull(value);

        this.value = value;
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}