package com.rafaelsousa.algashop.ordering.domain.valueobject;

import java.util.Objects;

public record Document(String value) {

    public Document {
        Objects.requireNonNull(value);

        if (value.isBlank()) {
            throw new IllegalArgumentException();
        }
    }

    public static Document of(String value) {
        return new Document(value);
    }

    @Override
    public String toString() {
        return this.value;
    }
}