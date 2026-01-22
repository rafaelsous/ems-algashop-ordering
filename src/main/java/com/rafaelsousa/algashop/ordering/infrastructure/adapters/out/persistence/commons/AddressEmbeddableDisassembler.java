package com.rafaelsousa.algashop.ordering.infrastructure.adapters.out.persistence.commons;

import com.rafaelsousa.algashop.ordering.core.domain.model.commons.Address;
import com.rafaelsousa.algashop.ordering.core.domain.model.commons.ZipCode;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AddressEmbeddableDisassembler {

    public static Address toDomain(AddressEmbeddable address) {
        Objects.requireNonNull(address);

        return Address.builder()
                .street(address.getStreet())
                .number(address.getNumber())
                .complement(address.getComplement())
                .neighborhood(address.getNeighborhood())
                .city(address.getCity())
                .state(address.getState())
                .zipCode(ZipCode.of(address.getZipCode()))
                .build();
    }
}