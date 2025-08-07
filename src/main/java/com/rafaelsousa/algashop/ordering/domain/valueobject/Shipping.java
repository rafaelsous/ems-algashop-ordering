package com.rafaelsousa.algashop.ordering.domain.valueobject;

import lombok.Builder;

import java.time.LocalDate;
import java.util.Objects;

@Builder(toBuilder = true)
public record Shipping(Recipient recipient,
                       Money cost,
                       LocalDate expectedDate,
                       Address address) {

    @Builder(toBuilder = true)
    public Shipping {
        Objects.requireNonNull(recipient);
        Objects.requireNonNull(cost);
        Objects.requireNonNull(expectedDate);
        Objects.requireNonNull(address);
    }

    public static Shipping of(Recipient recipient, Money cost, LocalDate expectedDate, Address address) {
        return new Shipping(recipient, cost, expectedDate, address);
    }
}