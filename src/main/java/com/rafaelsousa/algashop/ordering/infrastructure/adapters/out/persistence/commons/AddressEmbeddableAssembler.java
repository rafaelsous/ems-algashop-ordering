package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.commons;

import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Address;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressEmbeddableAssembler {

    public static AddressEmbeddable fromPersistence(Address address) {
        Objects.requireNonNull(address);

        return AddressEmbeddable.builder()
                .street(address.street())
                .number(address.number())
                .complement(address.complement())
                .neighborhood(address.neighborhood())
                .city(address.city())
                .state(address.state())
                .zipCode(address.zipCode().value())
                .build();
    }
}