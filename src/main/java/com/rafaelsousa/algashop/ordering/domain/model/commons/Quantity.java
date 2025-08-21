package com.rafaelsousa.algashop.ordering.domain.model.commons;

import java.util.Objects;

public record Quantity(Integer value) implements Comparable<Quantity> {
    public static final Quantity ZERO = new Quantity(0);

    public Quantity {
        Objects.requireNonNull(value);

        if (value < 0) {
            throw new IllegalArgumentException();
        }
    }

    public static Quantity of(Integer value) {
        return new Quantity(value);
    }

    public Quantity add(Quantity other) {
        Objects.requireNonNull(other);

        return new Quantity(this.value() + other.value());
    }

    @Override
    public int compareTo(Quantity o) {
        return this.value().compareTo(o.value());
    }

    @Override
    public String toString() {
        return this.value().toString();
    }
}