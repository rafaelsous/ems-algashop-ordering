package com.rafaelsousa.algashop.ordering.domain.model.product;

import java.util.Objects;

public record ProductName(String value) {

    public ProductName {
        Objects.requireNonNull(value);

        if (value.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

    public static ProductName of(String value) {
        return new ProductName(value);
    }

    @Override
    public String toString() {
        return this.value();
    }
}