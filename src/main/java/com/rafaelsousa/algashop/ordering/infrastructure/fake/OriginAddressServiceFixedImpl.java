package com.rafaelsousa.algashop.ordering.infrastructure.fake;

import com.rafaelsousa.algashop.ordering.domain.model.service.OriginAddressService;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.Address;
import com.rafaelsousa.algashop.ordering.domain.model.valueobject.ZipCode;
import org.springframework.stereotype.Component;

@Component
public class OriginAddressServiceFixedImpl implements OriginAddressService {

    @Override
    public Address originAddress() {
        return Address.builder()
                .street("Bourbon Street")
                .number("1122")
                .neighborhood("North Ville")
                .city("York")
                .state("South California")
                .zipCode(ZipCode.of("12345"))
                .build();
    }
}