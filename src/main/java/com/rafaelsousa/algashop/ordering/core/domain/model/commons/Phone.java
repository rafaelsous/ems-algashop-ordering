package com.rafaelsousa.algashop.ordering.core.domain.model.commons;

import java.util.Objects;

public record Phone(String value) {

    public Phone(String value) {
        Objects.requireNonNull(value);

        if (value.isBlank()) {
            throw new IllegalArgumentException();
        }

        this.value = value.trim();
    }

    public static Phone of(String value) {
        return new Phone(value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}