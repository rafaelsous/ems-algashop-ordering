package com.rafaelsousa.algashop.ordering.domain.valueobject;

import lombok.Builder;

import java.util.Objects;

public record ShippingInfo(FullName fullName,
                           Document document,
                           Phone phone,
                           Address address) {

    @Builder(toBuilder = true)
    public ShippingInfo {
        Objects.requireNonNull(fullName);
        Objects.requireNonNull(document);
        Objects.requireNonNull(phone);
        Objects.requireNonNull(address);
    }

    public static ShippingInfo of(FullName fullName, Document document, Phone phone, Address address) {
        return new ShippingInfo(fullName, document, phone, address);
    }
}