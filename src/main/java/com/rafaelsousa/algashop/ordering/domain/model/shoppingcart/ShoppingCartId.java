package com.rafaelsousa.algashop.ordering.domain.model.shoppingcart;

import com.rafaelsousa.algashop.ordering.domain.model.IdGenerator;

import java.util.Objects;
import java.util.UUID;

public record ShoppingCartId(UUID value) {

    public ShoppingCartId {
        Objects.requireNonNull(value);
    }

    public ShoppingCartId() {
        this(IdGenerator.generateTimeBasedUUID());
    }

    @Override
    public String toString() {
        return value.toString();
    }
}