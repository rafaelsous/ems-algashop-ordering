package com.rafaelsousa.algashop.ordering.domain.valueobject;

import lombok.Builder;

import java.util.Objects;

public record BillingInfo(FullName fullName,
                          Document document,
                          Phone phone,
                          Address address) {

    @Builder(toBuilder = true)
    public BillingInfo {
        Objects.requireNonNull(fullName);
        Objects.requireNonNull(document);
        Objects.requireNonNull(phone);
        Objects.requireNonNull(address);
    }

    public static BillingInfo of(FullName fullName, Document document, Phone phone, Address address) {
        return new BillingInfo(fullName, document, phone, address);
    }
}