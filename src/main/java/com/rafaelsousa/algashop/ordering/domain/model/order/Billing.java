package com.rafaelsousa.algashop.ordering.domain.model.order;

import com.rafaelsousa.algashop.ordering.domain.model.commons.*;
import lombok.Builder;

import java.util.Objects;

public record Billing(FullName fullName,
                      Document document,
                      Phone phone,
                      Email email,
                      Address address) {

    @Builder(toBuilder = true)
    public Billing {
        Objects.requireNonNull(fullName);
        Objects.requireNonNull(document);
        Objects.requireNonNull(phone);
        Objects.requireNonNull(email);
        Objects.requireNonNull(address);
    }

    public static Billing of(FullName fullName, Document document, Phone phone, Email email, Address address) {
        return new Billing(fullName, document, phone, email, address);
    }
}