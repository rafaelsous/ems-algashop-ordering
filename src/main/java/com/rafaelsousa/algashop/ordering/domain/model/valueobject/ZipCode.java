package com.rafaelsousa.algashop.ordering.domain.model.valueobject;

import java.util.Objects;

public record ZipCode(String value) {
    public ZipCode {
        Objects.requireNonNull(value);

        if (value.isBlank()) {
            throw new IllegalArgumentException();
        }

        if (value.length() != 5) {
            throw new IllegalArgumentException();
        }
    }

    public static ZipCode of (String value) {
        return new ZipCode(value);
    }

    @Override
    public String toString() {
        return value;
    }
}