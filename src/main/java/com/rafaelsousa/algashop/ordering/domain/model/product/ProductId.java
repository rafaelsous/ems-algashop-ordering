package com.rafaelsousa.algashop.ordering.domain.model.product;

import com.rafaelsousa.algashop.ordering.domain.model.IdGenerator;

import java.util.Objects;
import java.util.UUID;

public record ProductId(UUID value) {

    public ProductId {
        Objects.requireNonNull(value);
    }

    public ProductId() {
        this(IdGenerator.generateTimeBasedUUID());
    }

    @Override
    public String toString() {
        return this.value.toString();
    }
}